package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXOMappingResponseDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.*;
import java.util.stream.Stream;

import static uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants.ZOOMA_CONFIDENCE_HIGH;

@Service
public class MatchmakerServiceImpl implements MatchmakerService {

    private static final Logger log = LoggerFactory.getLogger(MatchmakerService.class);

    @Autowired
    private MappingSuggestionsService mappingSuggestionsService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ZoomaService zoomaService;

    @Autowired
    private OntologyTermService ontologyTermService;

    @Autowired
    private OLSService olsService;

    @Autowired
    private OXOService oxoService;

    @Autowired
    private UserService userService;

    @Override
    @Async
    public void runMatchmaking(String sourceId, Project project) {
        log.info("Running auto-mapping for source: {}", sourceId);
        User robotUser = userService.retrieveRobotUser();
        Stream<Entity> entityStream = entityService.retrieveEntitiesForSource(sourceId);
        entityStream.forEach(entity -> this.autoMap(entity, project, robotUser));
        entityStream.close();
    }

    private void autoMap(Entity entity, Project project, User user) {
        /**
         * Retrieve annotations from Zooma from datasources stored in the project
         */
        List<ZoomaResponseDto> zoomaResults = zoomaService.annotate(entity.getName(), project.getDatasources(), null);

        /**
         * Retrieve annotations from Zooma from ontologies stored in the project
         */
        zoomaResults.addAll(zoomaService.annotate(entity.getName(), null, project.getOntologies()));

        List<String> highConfidenceIRIs = new ArrayList<>();
        Set<String> finalIRIs = new HashSet<>();
        for (ZoomaResponseDto zoomaResponseDto : zoomaResults) {
            if (zoomaResponseDto.getSemanticTags().size() > 1) {
                log.warn("Found suggestion with combined terms: {} | {}", entity, zoomaResponseDto.getSemanticTags());
                continue;
            }

            String suggestedTermIRI = zoomaResponseDto.getSemanticTags().get(0);
            /**
             * Retain high confidence terms to attempt exact mapping.
             */
            if (zoomaResponseDto.getConfidence().equalsIgnoreCase(ZOOMA_CONFIDENCE_HIGH)) {
                highConfidenceIRIs.add(suggestedTermIRI);
            }

            /**
             * Retain only suggestions pertaining to the ontologies of interest (if these have been specified)
             */
            if (project.getOntologies() != null) {
                if (project.getOntologies().contains(CurationUtil.ontoFromIRI(suggestedTermIRI))) {
                    finalIRIs.add(suggestedTermIRI);
                }
            } else {
                finalIRIs.add(suggestedTermIRI);
            }
        }

        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        List<OntologyTerm> termsCreated = new ArrayList<>();
        for (String iri : finalIRIs) {
            OntologyTerm ontologyTerm = ontologyTermService.createTerm(iri, project);
            if (ontologyTerm != null) {
                termsCreated.add(ontologyTerm);
                mappingSuggestionsService.createMappingSuggestion(entity, ontologyTerm, provenance);
            }
        }

        if (!termsCreated.isEmpty()) {
            entity = entityService.updateMappingStatus(entity, EntityStatus.SUGGESTIONS_PROVIDED);
            if (entity == null) {
                return;
            }
        }

        List<OntologyTerm> newTerms = findExactMapping(entity, termsCreated, highConfidenceIRIs, project, provenance);
        if (!newTerms.isEmpty() && termsCreated.isEmpty()) {
            entity = entityService.updateMappingStatus(entity, EntityStatus.SUGGESTIONS_PROVIDED);
        }
        termsCreated.addAll(newTerms);
        mappingSuggestionsService.deleteMappingSuggestionsExcluding(entity, termsCreated);
    }

    private List<OntologyTerm> findExactMapping(Entity entity, List<OntologyTerm> termsCreated, List<String> highConfidenceIRIs, Project project, Provenance provenance) {
        List<OntologyTerm> ontoSuggestions = new ArrayList<>();

        if (!entity.getMappingStatus().equals(EntityStatus.UNMAPPED)) {
            log.warn("Entity has an existing mapping.");
        }

        for (OntologyTerm ontologyTerm : termsCreated) {
            if (highConfidenceIRIs.contains(ontologyTerm.getIri())) {
                mappingService.createMapping(entity, ontologyTerm, provenance);
                entity = entityService.updateMappingStatus(entity, EntityStatus.AUTO_MAPPED);
                log.info("Found high confidence mapping for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                return ontoSuggestions;
            }
        }

        for (OntologyTerm ontologyTerm : termsCreated) {
            if (entity.getName().equalsIgnoreCase(ontologyTerm.getLabel())) {
                mappingService.createMapping(entity, ontologyTerm, provenance);
                entity = entityService.updateMappingStatus(entity, EntityStatus.AUTO_MAPPED);
                log.info("Found exact text matching for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                return ontoSuggestions;
            }
        }

        for (String iri : highConfidenceIRIs) {
            String ontoId = CurationUtil.ontoFromIRI(iri);
            List<OLSTermDto> olsTerms = olsService.retrieveTerms(ontoId, iri);
            if (olsTerms.isEmpty()) {
                log.warn("Found no OLS results. Cannot continue mapping for: {}", entity.getName());
                continue;
            }
            if (olsTerms.size() > 1) {
                log.warn("Found {} OLS results. Using only the first one to map to: {}", olsTerms.size(), entity.getName());
            }

            /**
             * TODO: Discuss why are so many calls to OLS required
             */
            List<OXOMappingResponseDto> oxoMappings = oxoService.findMapping(Arrays.asList(new String[]{olsTerms.get(0).getCurie()}), project.getOntologies());
            for (OXOMappingResponseDto oxoMappingResponseDto : oxoMappings) {
                String targetOntoId = oxoMappingResponseDto.getTargetPrefix().toLowerCase();
                olsTerms = olsService.retrieveTerms(targetOntoId, oxoMappingResponseDto.getCurie());
                if (olsTerms.isEmpty()) {
                    continue;
                }
                String resultIri = olsTerms.get(0).getIri();
                OntologyTerm ontologyTerm = ontologyTermService.createTerm(resultIri, project);
                if (ontologyTerm != null) {
                    ontoSuggestions.add(ontologyTerm);
                    mappingSuggestionsService.createMappingSuggestion(entity, ontologyTerm, provenance);
                }
            }
        }

        return ontoSuggestions;
    }

}

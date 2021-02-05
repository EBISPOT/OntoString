package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.repository.EntityRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXOMappingResponseDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.*;
import java.util.stream.Stream;

import static uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants.ZOOMA_CONFIDENCE_HIGH;

@Service
public class MappingServiceImpl implements MappingService {

    private static final Logger log = LoggerFactory.getLogger(MappingService.class);

    @Autowired
    private MappingRepository mappingRepository;

    @Autowired
    private MappingSuggestionsService mappingSuggestionsService;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private ZoomaService zoomaService;

    @Autowired
    private OntologyTermService ontologyTermService;

    @Autowired
    private OLSService olsService;

    @Autowired
    private OXOService oxoService;

    @Override
    @Async
    public void runAutoMapping(String sourceId, Project project) {
        log.info("Running auto-mapping for source: {}", sourceId);
        Stream<Entity> entityStream = entityRepository.readBySourceId(sourceId);
        entityStream.forEach(entity -> this.autoMap(entity, project));
        entityStream.close();
    }

    private void autoMap(Entity entity, Project project) {
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
            if (zoomaResponseDto.getConfidence().equalsIgnoreCase(ZOOMA_CONFIDENCE_HIGH)) {
                highConfidenceIRIs.add(suggestedTermIRI);
            }
            if (project.getOntologies() != null && project.getOntologies().contains(CurationUtil.ontoFromIRI(suggestedTermIRI))) {
                finalIRIs.add(suggestedTermIRI);
            }
        }

        List<OntologyTerm> termsCreated = new ArrayList<>();
        for (String iri : finalIRIs) {
            OntologyTerm ontologyTerm = ontologyTermService.createTerm(iri);
            termsCreated.add(ontologyTerm);
            mappingSuggestionsService.createMappingSuggestion(entity, ontologyTerm);
        }

        termsCreated.addAll(findExactMapping(entity, termsCreated, highConfidenceIRIs, project));
        mappingSuggestionsService.deleteMappingSuggestionsExcluding(entity, termsCreated);
    }

    private List<OntologyTerm> findExactMapping(Entity entity, List<OntologyTerm> termsCreated, List<String> highConfidenceIRIs, Project project) {
        List<OntologyTerm> ontoSuggestions = new ArrayList<>();

        for (OntologyTerm ontologyTerm : termsCreated) {
            if (highConfidenceIRIs.contains(ontologyTerm.getIri())) {
                this.createMapping(entity, ontologyTerm);
                log.info("Found high confidence mapping for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                return ontoSuggestions;
            }
        }

        for (OntologyTerm ontologyTerm : termsCreated) {
            if (entity.getName().equalsIgnoreCase(ontologyTerm.getLabel())) {
                this.createMapping(entity, ontologyTerm);
                log.info("Found exact text matching for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                return ontoSuggestions;
            }
        }

        for (String iri : highConfidenceIRIs) {
            String ontoId = CurationUtil.ontoFromIRI(iri);
            List<OLSTermDto> olsTerms = olsService.retrieveTerms(ontoId, iri);
            if (olsTerms.size() > 1) {
                log.warn("Found {} OLS results. Using only the first one to map to: {}", olsTerms.size(), entity.getName());
            }
            if (olsTerms.isEmpty()) {
                log.warn("Found no OLS results. Cannot continue mapping for: {}", entity.getName());
                continue;
            }

            List<OXOMappingResponseDto> oxoMappings = oxoService.findMapping(Arrays.asList(new String[]{olsTerms.get(0).getCurie()}), project.getOntologies());
            for (OXOMappingResponseDto oxoMappingResponseDto : oxoMappings) {
                String targetOntoId = oxoMappingResponseDto.getTargetPrefix().toLowerCase();
                olsTerms = olsService.retrieveTerms(targetOntoId, oxoMappingResponseDto.getCurie());
                if (olsTerms.isEmpty()) {
                    continue;
                }
                String resultIri = olsTerms.get(0).getIri();
                OntologyTerm ontologyTerm = ontologyTermService.createTerm(resultIri);
                ontoSuggestions.add(ontologyTerm);
                mappingSuggestionsService.createMappingSuggestion(entity, ontologyTerm);
            }
        }

        return ontoSuggestions;
    }

    /**
     * TODO: Implement
     */
    private void createMapping(Entity entity, OntologyTerm ontologyTerm) {
    }
}

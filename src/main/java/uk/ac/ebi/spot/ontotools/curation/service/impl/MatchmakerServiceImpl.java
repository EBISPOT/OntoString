package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
    private UserService userService;

    @Override
    @Async(value = "applicationTaskExecutor")
    public void runMatchmaking(String sourceId, Project project) {
        log.info("Running auto-mapping for source: {}", sourceId);
        User robotUser = userService.retrieveRobotUser();
        project.setOntologies(CurationUtil.configListtoLowerCase(project.getOntologies()));
        project.setDatasources(CurationUtil.configListtoLowerCase(project.getDatasources()));
        project.setPreferredMappingOntologies(CurationUtil.listToLowerCase(project.getPreferredMappingOntologies()));

        long sTime = System.currentTimeMillis();
        Stream<Entity> entityStream = entityService.retrieveEntitiesForSource(sourceId);
        entityStream.forEach(entity -> this.autoMap(entity, project, robotUser));
        entityStream.close();
        long eTime = System.currentTimeMillis();
        log.info("[{}] Auto-mapping done in {}s", sourceId, (eTime - sTime) / 1000);
    }

    private void autoMap(Entity entity, Project project, User user) {
        List<String> projectDatasources = CurationUtil.configForField(entity, project.getDatasources());
        List<String> projectOntologies = CurationUtil.configForField(entity, project.getOntologies());

        /**
         * Retrieve annotations from Zooma from datasources stored in the project
         */
        List<ZoomaResponseDto> zoomaResults = zoomaService.annotate(entity.getName(), projectDatasources, null);

        /**
         * Retrieve annotations from Zooma from ontologies stored in the project
         */
        zoomaResults.addAll(zoomaService.annotate(entity.getName(), null, projectOntologies));

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
            if (projectOntologies != null) {
                if (projectOntologies.contains(CurationUtil.ontoFromIRI(suggestedTermIRI).toLowerCase())) {
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

                if (highConfidenceIRIs.contains(iri)) {
                    if (entity.getMappingStatus().equals(EntityStatus.UNMAPPED) || entity.getMappingStatus().equals(EntityStatus.SUGGESTIONS_PROVIDED)) {
                        mappingService.createMapping(entity, ontologyTerm, provenance);
                        entity = entityService.updateMappingStatus(entity, EntityStatus.AUTO_MAPPED);
                        log.info("Found high confidence mapping for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                    }
                } else {
                    if (entity.getName().equalsIgnoreCase(ontologyTerm.getLabel()) &&
                            (entity.getMappingStatus().equals(EntityStatus.UNMAPPED) || entity.getMappingStatus().equals(EntityStatus.SUGGESTIONS_PROVIDED))) {
                        mappingService.createMapping(entity, ontologyTerm, provenance);
                        entity = entityService.updateMappingStatus(entity, EntityStatus.AUTO_MAPPED);
                        log.info("Found exact text matching for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                    }
                }
            }
        }

        if (!termsCreated.isEmpty() && entity.getMappingStatus().equals(EntityStatus.UNMAPPED)) {
            entity = entityService.updateMappingStatus(entity, EntityStatus.SUGGESTIONS_PROVIDED);
            if (entity == null) {
                return;
            }
        }

        log.info(" -- Final IRIs and terms created: {}", finalIRIs, termsCreated);
        mappingSuggestionsService.deleteMappingSuggestionsExcluding(entity, termsCreated);
    }

}

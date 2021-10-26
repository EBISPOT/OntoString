package uk.ac.ebi.spot.ontostring.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.service.*;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.domain.log.MatchmakingLogEntry;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.ProjectContext;
import uk.ac.ebi.spot.ontostring.domain.Provenance;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontostring.service.*;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;

import java.util.*;
import java.util.stream.Stream;

import static uk.ac.ebi.spot.ontostring.constants.CurationConstants.ZOOMA_CONFIDENCE_HIGH;

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

    @Autowired
    private MatchmakingLogService matchmakingLogService;

    @Override
    @Async(value = "applicationTaskExecutor")
    public void runMatchmaking(String sourceId, Project project) {
        log.info("Running auto-mapping for source: {}", sourceId);
        User robotUser = userService.retrieveRobotUser();
        String batchId = matchmakingLogService.createBatch(project.getId());

        long sTime = System.currentTimeMillis();
        Stream<Entity> entityStream = entityService.retrieveEntitiesForSource(sourceId);
        entityStream.forEach(entity -> this.autoMap(entity, project, robotUser, batchId));
        entityStream.close();
        long eTime = System.currentTimeMillis();
        log.info("[{}] Auto-mapping done in {}s", sourceId, (eTime - sTime) / 1000);
    }

    @Override
    public void autoMap(Entity entity, Project project, User user, String batchId) {
        if (entity.getMappingStatus().equals(EntityStatus.MANUALLY_MAPPED) ||
                entity.getMappingStatus().equals(EntityStatus.AUTO_MAPPED)) {
            log.info("Entity [{}] has mapping status [{}]. Will not attempt re-mapping it.", entity.getName(), entity.getMappingStatus().name());
            return;
        }

        Pair<ProjectContext, Boolean> projectContextInfo = CurationUtil.findContext(entity.getContext(), project);
        if (!projectContextInfo.getRight()) {
            log.error("Cannot find context [{}] for entity [{}] in project: {}", entity.getContext(), entity.getName(), project.getId());
        }
        ProjectContext projectContext = projectContextInfo.getLeft();

        List<String> projectDatasources = projectContext.getDatasources();
        List<String> projectOntologies = projectContext.getOntologies();

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



            log.warn("Processing suggestion: {}", suggestedTermIRI);

            /**
             * Retain high confidence terms to attempt exact mapping.
             */
            if (zoomaResponseDto.getConfidence().equalsIgnoreCase(ZOOMA_CONFIDENCE_HIGH) && !highConfidenceIRIs.contains(suggestedTermIRI)) {
                highConfidenceIRIs.add(suggestedTermIRI);
            }

            /**
             * Retain only suggestions pertaining to the ontologies of interest (if these have been specified)
             */
            if (projectOntologies != null) {
                if (projectOntologies.contains(CurationUtil.ontoFromIRI(suggestedTermIRI).toLowerCase())) {
                    log.warn("Entity {}: Term {} was found in ontology {} which is in the list of project ontologies; this suggestion will be kept",
                            entity.getName(), suggestedTermIRI, CurationUtil.ontoFromIRI(suggestedTermIRI).toLowerCase());
                    if (!finalIRIs.contains(suggestedTermIRI)) {
                        finalIRIs.add(suggestedTermIRI);
                    }
                } else {
                    log.warn("Entity {}: Term {} was found in ontology {} which is not in the list of project ontologies; this suggestion will be discarded",
                            entity.getName(), suggestedTermIRI, CurationUtil.ontoFromIRI(suggestedTermIRI).toLowerCase());
                }
            } else {
                log.warn("Project has no ontologies configured; term {} from ontology {} kept by default",
                        suggestedTermIRI, CurationUtil.ontoFromIRI(suggestedTermIRI).toLowerCase());
                if (!finalIRIs.contains(suggestedTermIRI)) {
                    finalIRIs.add(suggestedTermIRI);
                }
            }
        }

        matchmakingLogService.logEntry(new MatchmakingLogEntry(null, batchId, entity.getId(),
                entity.getName(), highConfidenceIRIs, finalIRIs));
        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        List<OntologyTerm> termsCreated = new ArrayList<>();
        for (String iri : finalIRIs) {
            OntologyTerm ontologyTerm = ontologyTermService.createTerm(iri, projectContext);
            if (ontologyTerm != null) {
                termsCreated.add(ontologyTerm);
                mappingSuggestionsService.createMappingSuggestion(entity, ontologyTerm, provenance);

                if (entity.getMappingStatus().equals(EntityStatus.MANUALLY_MAPPED)) {
                    continue;
                }
                if (entity.getMappingStatus().equals(EntityStatus.AUTO_MAPPED)) {
                    if (highConfidenceIRIs.contains(ontologyTerm.getIri())) {
                        mappingService.addMapping(entity, ontologyTerm, provenance);
                        log.info("Found high confidence mapping for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                    } else {
                        if (entity.getName().equalsIgnoreCase(ontologyTerm.getLabel())) {
                            mappingService.addMapping(entity, ontologyTerm, provenance);
                            log.info("Found high confidence mapping for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                        }
                    }
                    continue;
                }

                if (highConfidenceIRIs.contains(ontologyTerm.getIri())) {
                    mappingService.createMapping(entity, Arrays.asList(new OntologyTerm[]{ontologyTerm}), provenance);
                    entity = entityService.updateMappingStatus(entity, EntityStatus.AUTO_MAPPED);
                    log.info("Found high confidence mapping for [{}] in: {}", entity.getName(), ontologyTerm.getIri());
                } else {
                    if (entity.getName().equalsIgnoreCase(ontologyTerm.getLabel())) {
                        mappingService.createMapping(entity, Arrays.asList(new OntologyTerm[]{ontologyTerm}), provenance);
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
        mappingSuggestionsService.deleteMappingSuggestionsExcluding(entity, termsCreated, provenance);
    }

}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.repository.EntityRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.ZoomaService;
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
    private EntityRepository entityRepository;

    private ZoomaService zoomaService;

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
        Map<String, List<String>> zoomaDSResults = zoomaService.annotate(entity.getName(), project.getDatasources(), null);

        /**
         * Retrieve annotations from Zooma from ontologies stored in the project
         */
        Map<String, List<String>> zoomaResults = zoomaService.annotate(entity.getName(), null, project.getOntologies());

        List<String> highConfidenceIRIs = new ArrayList<>();
        Set<String> finalIRIs = new HashSet<>();
        for (String confidence : zoomaDSResults.keySet()) {
            if (zoomaDSResults.get(confidence).size() > 1) {
                log.warn("Found suggestion with combined terms: {} | {}", entity, zoomaDSResults.get(confidence));
                continue;
            }

            String suggestedTermIRI = zoomaDSResults.get(confidence).get(0);
            if (confidence.equalsIgnoreCase(ZOOMA_CONFIDENCE_HIGH)) {
                highConfidenceIRIs.add(suggestedTermIRI);
            }
            if (project.getOntologies() != null && project.getOntologies().contains(CurationUtil.ontoFromIRI())) {
                finalIRIs.add(suggestedTermIRI);
            }
        }

        for (String iri : finalIRIs) {
            /**
             * TODO:
             * - Create ontology term
             * - Create mapping suggestion
             */
        }

        /**
         * TODO: Find automatic mapping
         */
        /**
         * TODO: Delete previous mapping suggestions for the terms just created
         */
    }
}

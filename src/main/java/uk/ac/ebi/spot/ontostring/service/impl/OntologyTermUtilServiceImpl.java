package uk.ac.ebi.spot.ontostring.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.constants.TermStatus;
import uk.ac.ebi.spot.ontostring.domain.Provenance;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.mapping.*;
import uk.ac.ebi.spot.ontostring.repository.EntityRepository;
import uk.ac.ebi.spot.ontostring.repository.MappingRepository;
import uk.ac.ebi.spot.ontostring.repository.OntologyTermContextRepository;
import uk.ac.ebi.spot.ontostring.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontostring.service.OntologyTermUtilService;
import uk.ac.ebi.spot.ontostring.util.ContentCompiler;
import uk.ac.ebi.spot.ontostring.domain.mapping.*;

import java.util.*;
import java.util.stream.Stream;

@Service
public class OntologyTermUtilServiceImpl implements OntologyTermUtilService {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermUtilService.class);

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private MappingRepository mappingRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private OntologyTermContextRepository ontologyTermContextRepository;

    @Override
    public void actionTerms(String projectId, String context, String status, String comment, User user) {
        log.info("Updating status for terms: {} | {} | {}", projectId, context, status);
        Stream<OntologyTermContext> ontologyTermContextStream = ontologyTermContextRepository.readByHasMappingAndProjectIdAndContextAndStatus(true, projectId, context, status);
        ontologyTermContextStream.forEach(ontologyTermContext -> this.updateStatus(ontologyTermContext, status, comment, user));
        ontologyTermContextStream.close();
    }

    @Override
    public String exportOntologyTerms(String projectId, String context, String status) {
        ContentCompiler contentCompiler = new ContentCompiler();
        List<OntologyTermContext> ontologyTermContexts = ontologyTermContextRepository.findByHasMappingAndProjectIdAndContextAndStatus(true, projectId, context, status);

        List<Entity> entities = entityRepository.findByProjectIdAndContext(projectId, context);
        Map<String, Entity> entityMap = new LinkedHashMap<>();
        for (Entity entity : entities) {
            entityMap.put(entity.getId(), entity);
        }

        List<Mapping> mappings = mappingRepository.findByProjectIdAndContext(projectId, context);
        Map<String, List<String>> mappingMap = new LinkedHashMap<>();
        for (Mapping mapping : mappings) {
            for (String ontoTermId : mapping.getOntologyTermIds()) {
                List<String> list = mappingMap.containsKey(ontoTermId) ? mappingMap.get(ontoTermId) : new ArrayList<>();
                Entity entity = entityMap.get(mapping.getEntityId());
                if (!list.contains(entity.getName())) {
                    list.add(entity.getName());
                }
                mappingMap.put(ontoTermId, list);
            }
        }

        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            OntologyTerm ontologyTerm = ontologyTermRepository.findById(ontologyTermContext.getOntologyTermId()).get();
            List<String> entityNames = mappingMap.get(ontologyTerm.getId());
            contentCompiler.addOntologyTerm(ontologyTerm, StringUtils.join(entityNames, " | "));
        }

        return contentCompiler.getContent();
    }

    @Override
    public Map<OntologyTerm, Map<String, String>> retrieveEntityData(List<String> ontoTermIds, List<OntologyTermContext> ontologyTermContexts, String projectId, String context) {
        List<Entity> entities = entityRepository.findByProjectIdAndContext(projectId, context);
        Map<String, String> entityMap = new LinkedHashMap<>();
        for (Entity entity : entities) {
            entityMap.put(entity.getId(), entity.getName());
        }

        Map<OntologyTerm, Map<String, String>> result = new LinkedHashMap<>();
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            OntologyTerm ontologyTerm = ontologyTermRepository.findById(ontologyTermContext.getOntologyTermId()).get();
            List<Mapping> mappings = mappingRepository.findByIdIn(ontologyTermContext.getMappings());
            Map<String, String> entityNames = new HashMap<>();

            for (Mapping mapping : mappings) {
                entityNames.put(mapping.getEntityId(), entityMap.get(mapping.getEntityId()));
            }
            ontologyTerm.setStatus(ontologyTermContext.getStatus());
            result.put(ontologyTerm, entityNames);
        }
        return result;
    }

    private void updateStatus(OntologyTermContext ontologyTermContext, String oldStatus, String comment, User user) {
        String newStatus = TermStatus.AWAITING_IMPORT.name();
        if (oldStatus.equals(TermStatus.NEEDS_CREATION.name())) {
            newStatus = TermStatus.AWAITING_CREATION.name();
        }
        ontologyTermContext.setStatus(newStatus);
        ontologyTermContextRepository.save(ontologyTermContext);

        List<Mapping> mappings = mappingRepository.findByIdIn(ontologyTermContext.getMappings());
        for (Mapping mapping : mappings) {
            mapping.getComments().add(new Comment(comment, new Provenance(user.getName(), user.getEmail(), DateTime.now())));
            mappingRepository.save(mapping);
        }
    }
}

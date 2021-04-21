package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.*;
import uk.ac.ebi.spot.ontotools.curation.repository.EntityRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermUtilService;
import uk.ac.ebi.spot.ontotools.curation.util.ContentCompiler;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
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

    @Override
    public void actionTerms(String projectId, String context, String status, String comment, User user) {
        log.info("Updating status for terms: {} | {} | {}", projectId, context, status);
        Stream<OntologyTerm> ontologyTermStream = ontologyTermRepository.readByContexts_ProjectIdAndContexts_ContextAndContexts_Status(projectId, context, status);
        ontologyTermStream.forEach(ontologyTerm -> this.updateStatus(ontologyTerm, status, projectId, context, comment, user));
        ontologyTermStream.close();
    }

    @Override
    public String exportOntologyTerms(String projectId, String context, String status) {
        ContentCompiler contentCompiler = new ContentCompiler();
        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findByContexts_ProjectIdAndContexts_ContextAndContexts_Status(projectId, context, status);

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

        for (OntologyTerm ontologyTerm : ontologyTerms) {
            List<String> entityNames = mappingMap.get(ontologyTerm.getId());
            contentCompiler.addOntologyTerm(ontologyTerm, StringUtils.join(entityNames, " | "));
        }

        return contentCompiler.getContent();
    }

    private void updateStatus(OntologyTerm ontologyTerm, String oldStatus, String projectId, String context, String comment, User user) {
        String newStatus = TermStatus.AWAITING_IMPORT.name();
        if (oldStatus.equals(TermStatus.NEEDS_CREATION.name())) {
            newStatus = TermStatus.AWAITING_CREATION.name();
        }

        List<OntologyTermContext> ontologyTermContexts = ontologyTerm.getContexts();
        OntologyTermContext found = null;
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            if (ontologyTermContext.getProjectId().equals(projectId) && ontologyTermContext.getContext().equals(context)) {
                found = ontologyTermContext;
                break;
            }
        }

        if (found != null) {
            ontologyTerm.getContexts().remove(found);
            ontologyTerm.getContexts().add(new OntologyTermContext(projectId, context, newStatus));
            ontologyTermRepository.save(ontologyTerm);

            List<Mapping> mappings = mappingRepository.findByProjectIdAndContextAndOntologyTermIdsContains(projectId, context, ontologyTerm.getId());
            for (Mapping mapping : mappings) {
                mapping.getComments().add(new Comment(comment, new Provenance(user.getName(), user.getEmail(), DateTime.now())));
                mappingRepository.save(mapping);
            }
        } else {
            log.error("Unable to find context [{}] for ontology term: {} | {}", context, ontologyTerm.getCurie(), projectId);
        }
    }
}

package uk.ac.ebi.spot.ontostring.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.domain.mapping.*;
import uk.ac.ebi.spot.ontostring.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontostring.repository.EntityRepository;
import uk.ac.ebi.spot.ontostring.repository.MappingRepository;
import uk.ac.ebi.spot.ontostring.repository.MappingSuggestionRepository;
import uk.ac.ebi.spot.ontostring.service.EntityService;
import uk.ac.ebi.spot.ontostring.util.EntitiesCsvBuilder;
import uk.ac.ebi.spot.ontostring.util.OntologyTermCsvBuilder;

import java.util.*;
import java.util.stream.Stream;

@Service
public class EntityServiceImpl implements EntityService {

    private static final Logger log = LoggerFactory.getLogger(EntityService.class);

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private MappingRepository mappingRepository;

    @Autowired
    private MappingSuggestionRepository mappingSuggestionRepository;

    @Override
    public Entity createEntity(Entity entity) {
        log.debug("[{}] Creating entity: {}", entity.getSourceId(), entity.getName());
        Entity created = entityRepository.insert(entity);
        log.debug("[{}] Entity created: {}", created.getSourceId(), created.getId());
        return created;
    }

    @Override
    public Stream<Entity> retrieveEntitiesForSource(String sourceId) {
        return entityRepository.readBySourceId(sourceId);
    }

    @Override
    public Stream<Entity> streamEntitiesForProject(String projectId) {
        return entityRepository.readByProjectId(projectId);
    }

    @Override
    public Entity updateMappingStatus(Entity entity, EntityStatus mappingStatus) {
        log.info("Updating mapping status [{}]: {}", entity.getName(), mappingStatus);
        Optional<Entity> entityOptional = entityRepository.findById(entity.getId());
        if (!entityOptional.isPresent()) {
            log.error("Unable to find entity: {}", entity.getName());
            return null;
        }

        Entity existing = entityOptional.get();
        existing.setMappingStatus(mappingStatus);
        existing = entityRepository.save(existing);
        return existing;
    }

    @Override
    public Page<Entity> retrieveEntitiesForProject(String projectId, String prefix, String context, Pageable page) {
        log.debug("Retrieving entities [{}]: {} | {}", projectId, prefix, page.getPageNumber(), page.getPageSize());
        Page<Entity> entityPage = prefix == null ?
                (context == null ? entityRepository.findByProjectId(projectId, page) :
                        entityRepository.findByProjectIdAndContext(projectId, context, page)) :
                (context == null ? entityRepository.findByProjectIdAndNameLikeIgnoreCase(projectId, prefix, page) :
                        entityRepository.findByProjectIdAndContextAndNameLikeIgnoreCase(projectId, context, prefix, page));
        log.debug("Found {} entities.", entityPage.getContent().size());
        return entityPage;
    }

    @Override
    public Entity retrieveEntity(String entityId) {
        log.debug("Retrieving entity: {}", entityId);
        Optional<Entity> entityOptional = entityRepository.findById(entityId);
        if (!entityOptional.isPresent()) {
            log.error("Unable to find entity: {}", entityId);
            throw new EntityNotFoundException("Unable to find entity: " + entityId);
        }
        return entityOptional.get();
    }

    @Override
    public void moveEntities(String projectId, String fromContext, String toContext) {
        log.info("Moving entities in project [{}] from context [{}] to context: {}", projectId, fromContext, toContext);
        Stream<Entity> entityStream = entityRepository.readByProjectIdAndContext(projectId, fromContext);
        entityStream.forEach(entity -> updateContext(entity, toContext));
        entityStream.close();
    }

    private void updateContext(Entity entity, String toContext) {
        entity.setContext(toContext);
        entityRepository.save(entity);
    }

    @Override
    public String exportEntities(String projectId, String context) {

        EntitiesCsvBuilder csvBuilder = new EntitiesCsvBuilder();


        List<Entity> entities = entityRepository.findByProjectIdAndContext(projectId, context);
        Map<String, Entity> entityMap = new LinkedHashMap<>();
        for (Entity entity : entities) {
            entityMap.put(entity.getId(), entity);
        }


        List<Mapping> mappings = mappingRepository.findByProjectIdAndContext(projectId, context);
        Map<String, List<OntologyTerm>> mappingMap = new LinkedHashMap<>();
        for (Mapping mapping : mappings) {
            List<OntologyTerm> mappinglist = mappingMap.get(mapping.getEntityId());
            if(mappinglist == null) {
                mappinglist = new ArrayList<OntologyTerm>();
                mappingMap.put(mapping.getEntityId(), mappinglist);
            }
            for(OntologyTerm term : mapping.getOntologyTerms()) {
                mappinglist.add(term);
            }
        }

        List<MappingSuggestion> mappingSuggestions = mappingSuggestionRepository.findByProjectId(projectId);
        Map<String, List<OntologyTerm>> mappingSuggestionMap = new LinkedHashMap<>();
        for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
            List<OntologyTerm> suggestions = mappingSuggestionMap.get(mappingSuggestion.getEntityId());
            if(suggestions == null) {
                suggestions = new ArrayList<OntologyTerm>();
                mappingSuggestionMap.put(mappingSuggestion.getEntityId(), suggestions);
            }
            suggestions.add(mappingSuggestion.getOntologyTerm());
        }


        for (Entity entity : entities) {

            List<OntologyTerm> mappingslist = mappingMap.get(entity.getId());

            if(mappingslist != null) {
                for (OntologyTerm mapping : mappingslist) {
                    csvBuilder.addMappingRow(entity, mapping);
                }
            }

            List<OntologyTerm> suggestionslist = mappingSuggestionMap.get(entity.getId());

            if(suggestionslist != null) {
                for (OntologyTerm suggestion : suggestionslist) {
                    csvBuilder.addMappingRow(entity, suggestion);
                }
            }
        }

        return csvBuilder.getContent();
    }
}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.EntityRepository;
import uk.ac.ebi.spot.ontotools.curation.service.EntityService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class EntityServiceImpl implements EntityService {

    private static final Logger log = LoggerFactory.getLogger(EntityService.class);

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public Entity createEntity(Entity entity) {
        log.info("[{}] Creating entity: {}", entity.getSourceId(), entity.getName());
        Entity created = entityRepository.insert(entity);
        log.info("[{}] Entity created: {}", created.getSourceId(), created.getId());
        return created;
    }

    @Override
    public Stream<Entity> retrieveEntitiesForSource(String sourceId) {
        return entityRepository.readBySourceId(sourceId);
    }

    @Override
    public Entity updateMappingStatus(Entity entity, EntityStatus mappingStatus) {
        log.info("Updating mappping status [{}]: {}", entity.getName(), mappingStatus);
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
    public Page<Entity> retrieveEntitiesForSources(List<Source> sources, Pageable page) {
        log.info("Retrieving entities for {} sources: {} | {}", sources.size(), page.getPageNumber(), page.getPageSize());
        List<String> sourceIds = sources.stream().map(Source::getId).collect(Collectors.toList());
        Page<Entity> entityPage = entityRepository.findBySourceIdIn(sourceIds, page);
        log.info("Found {} entities.", entityPage.getContent().size());
        return entityPage;
    }

    @Override
    public Entity retrieveEntity(String entityId) {
        log.info("Retrieving entity: {}", entityId);
        Optional<Entity> entityOptional = entityRepository.findById(entityId);
        if (!entityOptional.isPresent()) {
            log.error("Unable to find entity: {}", entityId);
            throw new EntityNotFoundException("Unable to find entity: " + entityId);
        }
        return entityOptional.get();
    }

}

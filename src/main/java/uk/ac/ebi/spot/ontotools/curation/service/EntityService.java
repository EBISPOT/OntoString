package uk.ac.ebi.spot.ontotools.curation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;

import java.util.stream.Stream;

public interface EntityService {

    Entity createEntity(Entity entity);

    Stream<Entity> retrieveEntitiesForSource(String sourceId);

    Stream<Entity> streamEntitiesForProject(String projectId);

    Entity updateMappingStatus(Entity entity, EntityStatus mappingStatus);

    Page<Entity> retrieveEntitiesForProject(String projectId, String prefix, String context, Pageable page);

    Entity retrieveEntity(String entityId);

    void moveEntities(String projectId, String fromContext, String toContext);
}

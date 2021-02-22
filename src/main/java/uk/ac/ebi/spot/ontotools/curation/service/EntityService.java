package uk.ac.ebi.spot.ontotools.curation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;

import java.util.List;
import java.util.stream.Stream;

public interface EntityService {

    Entity createEntity(Entity entity);

    Stream<Entity> retrieveEntitiesForSource(String sourceId);

    Entity updateMappingStatus(Entity entity, EntityStatus mappingStatus);

    Page<Entity> retrieveEntitiesForProject(String projectId, Pageable page);

    Entity retrieveEntity(String entityId);
}

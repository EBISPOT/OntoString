package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;

import java.util.List;
import java.util.stream.Stream;

public interface EntityRepository extends MongoRepository<Entity, String> {

    Stream<Entity> readBySourceId(String sourceId);

    Page<Entity> findByProjectId(String projectId, Pageable page);

    Stream<Entity> readByProjectId(String projectId);

    Stream<Entity> readByProjectIdAndContext(String projectId, String context);

    Stream<Entity> readByProjectIdAndMappingStatusIn(String projectId, List<EntityStatus> statusList);

    Page<Entity> findByNameLikeIgnoreCase(String prefix, Pageable pageable);
}

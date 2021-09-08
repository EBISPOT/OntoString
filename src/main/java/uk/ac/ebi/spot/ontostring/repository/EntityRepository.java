package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;

import java.util.List;
import java.util.stream.Stream;

public interface EntityRepository extends MongoRepository<Entity, String> {

    Stream<Entity> readBySourceId(String sourceId);

    Page<Entity> findByProjectId(String projectId, Pageable page);

    Page<Entity> findByProjectIdAndContext(String projectId, String context, Pageable page);

    List<Entity> findByProjectIdAndContext(String projectId, String context);

    Stream<Entity> readByProjectId(String projectId);

    Stream<Entity> readByProjectIdAndContext(String projectId, String context);

    Stream<Entity> readByProjectIdAndMappingStatusIn(String projectId, List<EntityStatus> statusList);

    Page<Entity> findByProjectIdAndNameLikeIgnoreCase(String projectId, String prefix, Pageable pageable);

    Page<Entity> findByProjectIdAndContextAndNameLikeIgnoreCase(String projectId, String context, String prefix, Pageable pageable);
}

package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;

import java.util.List;
import java.util.stream.Stream;

public interface EntityRepository extends MongoRepository<Entity, String> {

    Stream<Entity> readBySourceId(String sourceId);

    Page<Entity> findBySourceIdIn(List<String> sourceIds, Pageable page);
}

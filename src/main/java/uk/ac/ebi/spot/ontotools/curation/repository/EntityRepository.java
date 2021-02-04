package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;

import java.util.stream.Stream;

public interface EntityRepository extends MongoRepository<Entity, String> {

    Stream<Entity> readBySourceId(String sourceId);
}

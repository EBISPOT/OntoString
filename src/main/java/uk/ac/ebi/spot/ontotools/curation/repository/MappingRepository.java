package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;

import java.util.List;
import java.util.Optional;

public interface MappingRepository extends MongoRepository<Mapping, String> {
    List<Mapping> findByEntityIdIn(List<String> entityIds);

    Optional<Mapping> findByEntityId(String entityId);
}

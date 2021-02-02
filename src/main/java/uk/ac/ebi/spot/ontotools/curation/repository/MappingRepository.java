package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;

import java.util.List;

public interface MappingRepository extends MongoRepository<Mapping, String> {
    List<Mapping> findByMappedTraitIdIn(List<String> traitIds);
}

package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Optional;

public interface OntologyTermRepository extends MongoRepository<OntologyTerm, String> {
    List<OntologyTerm> findByIdIn(List<String> ontoTermIds);

    Optional<OntologyTerm> findByIriHash(String iriHash);

    Optional<OntologyTerm> findByCurie(String curie);
}

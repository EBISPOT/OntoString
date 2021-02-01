package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;

import java.util.List;

public interface OntologyTermRepository extends MongoRepository<OntologyTerm, String> {
    List<OntologyTerm> findByIdIn(List<String> ontoTermIds);
}

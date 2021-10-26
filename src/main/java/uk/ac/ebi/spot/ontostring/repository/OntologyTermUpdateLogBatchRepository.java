package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.log.OntologyTermUpdateLogBatch;

public interface OntologyTermUpdateLogBatchRepository extends MongoRepository<OntologyTermUpdateLogBatch, String> {

}

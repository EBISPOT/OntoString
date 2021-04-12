package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.log.ImportLogBatch;

public interface ImportLogBatchRepository extends MongoRepository<ImportLogBatch, String> {

}

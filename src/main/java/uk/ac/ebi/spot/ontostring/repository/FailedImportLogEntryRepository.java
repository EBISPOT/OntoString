package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.log.FailedImportLogEntry;

public interface FailedImportLogEntryRepository extends MongoRepository<FailedImportLogEntry, String> {

}

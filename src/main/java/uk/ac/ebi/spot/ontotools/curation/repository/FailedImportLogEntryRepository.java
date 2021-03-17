package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.FailedImportLogEntry;

public interface FailedImportLogEntryRepository extends MongoRepository<FailedImportLogEntry, String> {

}

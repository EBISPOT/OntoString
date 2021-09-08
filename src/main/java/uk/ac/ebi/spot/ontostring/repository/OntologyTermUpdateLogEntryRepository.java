package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.log.OntologyTermUpdateLogEntry;

public interface OntologyTermUpdateLogEntryRepository extends MongoRepository<OntologyTermUpdateLogEntry, String> {

}

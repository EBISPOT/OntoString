package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.log.OntologyTermUpdateLogEntry;

public interface OntologyTermUpdateLogEntryRepository extends MongoRepository<OntologyTermUpdateLogEntry, String> {

}

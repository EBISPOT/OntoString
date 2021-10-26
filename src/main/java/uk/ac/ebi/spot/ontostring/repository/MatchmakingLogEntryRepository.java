package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.log.MatchmakingLogEntry;

public interface MatchmakingLogEntryRepository extends MongoRepository<MatchmakingLogEntry, String> {

}

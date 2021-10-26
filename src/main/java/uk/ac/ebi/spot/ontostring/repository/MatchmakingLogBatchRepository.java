package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.log.MatchmakingLogBatch;

public interface MatchmakingLogBatchRepository extends MongoRepository<MatchmakingLogBatch, String> {

}

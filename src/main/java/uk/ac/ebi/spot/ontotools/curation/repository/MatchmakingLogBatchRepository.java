package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.log.MatchmakingLogBatch;

public interface MatchmakingLogBatchRepository extends MongoRepository<MatchmakingLogBatch, String> {

}

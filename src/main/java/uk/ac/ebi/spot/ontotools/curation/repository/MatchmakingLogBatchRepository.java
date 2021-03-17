package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.MatchmakingLogBatch;

public interface MatchmakingLogBatchRepository extends MongoRepository<MatchmakingLogBatch, String> {

}

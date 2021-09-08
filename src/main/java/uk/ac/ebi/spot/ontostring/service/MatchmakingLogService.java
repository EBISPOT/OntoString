package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.log.MatchmakingLogEntry;

public interface MatchmakingLogService {

    void logEntry(MatchmakingLogEntry matchmakingLogEntry);

    String createBatch(String projectId);
}

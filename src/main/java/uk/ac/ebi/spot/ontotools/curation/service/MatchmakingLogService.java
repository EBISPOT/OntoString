package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.MatchmakingLogEntry;

public interface MatchmakingLogService {

    void logEntry(MatchmakingLogEntry matchmakingLogEntry);

    String createBatch(String projectId);
}

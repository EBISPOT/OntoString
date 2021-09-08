package uk.ac.ebi.spot.ontostring.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.domain.log.MatchmakingLogBatch;
import uk.ac.ebi.spot.ontostring.domain.log.MatchmakingLogEntry;
import uk.ac.ebi.spot.ontostring.repository.MatchmakingLogBatchRepository;
import uk.ac.ebi.spot.ontostring.repository.MatchmakingLogEntryRepository;
import uk.ac.ebi.spot.ontostring.service.MatchmakingLogService;

@Service
public class MatchmakingLogServiceImpl implements MatchmakingLogService {

    @Autowired
    private MatchmakingLogEntryRepository matchmakingLogEntryRepository;

    @Autowired
    private MatchmakingLogBatchRepository matchmakingLogBatchRepository;

    @Async(value = "applicationTaskExecutor")
    @Override
    public void logEntry(MatchmakingLogEntry matchmakingLogEntry) {
        matchmakingLogEntryRepository.insert(matchmakingLogEntry);
    }

    @Override
    public String createBatch(String projectId) {
        MatchmakingLogBatch matchmakingLogBatch = matchmakingLogBatchRepository.insert(new MatchmakingLogBatch(null, projectId, DateTime.now()));
        return matchmakingLogBatch.getId();
    }
}

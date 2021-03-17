package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.MatchmakingLogBatch;
import uk.ac.ebi.spot.ontotools.curation.domain.MatchmakingLogEntry;
import uk.ac.ebi.spot.ontotools.curation.repository.MatchmakingLogBatchRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.MatchmakingLogEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.service.MatchmakingLogService;

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

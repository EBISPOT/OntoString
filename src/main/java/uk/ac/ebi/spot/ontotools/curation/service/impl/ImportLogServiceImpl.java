package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.FailedImportLogEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.ImportLogBatch;
import uk.ac.ebi.spot.ontotools.curation.repository.FailedImportLogEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.ImportLogBatchRepository;
import uk.ac.ebi.spot.ontotools.curation.service.ImportLogService;

import java.util.Optional;

@Service
public class ImportLogServiceImpl implements ImportLogService {

    @Autowired
    private FailedImportLogEntryRepository failedImportLogEntryRepository;

    @Autowired
    private ImportLogBatchRepository importLogBatchRepository;

    @Async(value = "applicationTaskExecutor")
    @Override
    public void logEntry(FailedImportLogEntry failedImportLogEntry) {
        failedImportLogEntryRepository.insert(failedImportLogEntry);
    }

    @Override
    public String createBatch(String projectId, String sourceId) {
        ImportLogBatch importLogBatch = importLogBatchRepository.insert(new ImportLogBatch(null, projectId, sourceId, DateTime.now(),
                -1, -1, -1));
        return importLogBatch.getId();
    }

    @Async(value = "applicationTaskExecutor")
    @Override
    public void updateBatch(String batchId, long tTime, int count, int successful) {
        Optional<ImportLogBatch> importLogBatchOptional = importLogBatchRepository.findById(batchId);
        if (importLogBatchOptional.isPresent()) {
            ImportLogBatch importLogBatch = importLogBatchOptional.get();
            importLogBatch.setTotalTime(tTime);
            importLogBatch.setTotalCount(count);
            importLogBatch.setSuccessful(successful);
            importLogBatchRepository.save(importLogBatch);
        }
    }
}

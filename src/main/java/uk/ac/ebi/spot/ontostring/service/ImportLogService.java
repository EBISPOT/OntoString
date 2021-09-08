package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.log.FailedImportLogEntry;

public interface ImportLogService {

    void logEntry(FailedImportLogEntry failedImportLogEntry);

    String createBatch(String projectId, String sourceId);

    void updateBatch(String batchId, int tTime, int count, int successful);
}

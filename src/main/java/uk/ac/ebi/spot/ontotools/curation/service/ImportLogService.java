package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.FailedImportLogEntry;

public interface ImportLogService {

    void logEntry(FailedImportLogEntry failedImportLogEntry);

    String createBatch(String projectId, String sourceId);

    void updateBatch(String batchId, int tTime, int count, int successful);
}

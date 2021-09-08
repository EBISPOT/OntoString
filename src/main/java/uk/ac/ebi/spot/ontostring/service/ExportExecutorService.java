package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.ProjectExportRequest;

public interface ExportExecutorService {
    void addToQueue(ProjectExportRequest projectExportRequest);
}

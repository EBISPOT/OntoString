package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.ProjectExportRequest;

public interface ExportExecutorService {
    void addToQueue(ProjectExportRequest projectExportRequest);
}

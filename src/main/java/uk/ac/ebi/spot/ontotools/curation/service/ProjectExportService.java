package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.ProjectExportRequest;

public interface ProjectExportService {
    String registerRequest(String projectId);

    ProjectExportRequest getExportStatus(String projectId, String requestId);

    byte[] getExportContent(String projectId, String requestId);
}

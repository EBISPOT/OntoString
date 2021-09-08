package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.ProjectExportRequest;

public interface ProjectExportService {
    String registerRequest(String projectId);

    ProjectExportRequest getExportStatus(String projectId, String requestId);

    byte[] getExportContent(String projectId, String requestId);
}

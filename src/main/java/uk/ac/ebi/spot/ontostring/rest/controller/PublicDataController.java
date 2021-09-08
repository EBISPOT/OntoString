package uk.ac.ebi.spot.ontostring.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontostring.domain.ProjectExportRequest;
import uk.ac.ebi.spot.ontostring.service.ProjectExportService;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectExportStatusDto;

@RestController
@RequestMapping(value = GeneralCommon.API_PUBLIC + GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class PublicDataController {

    private static final Logger log = LoggerFactory.getLogger(PublicDataController.class);

    @Autowired
    private ProjectExportService projectExportService;

    /**
     * POST /v1/projects/{projectId}/export
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_EXPORT,
            produces = MediaType.TEXT_PLAIN_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public String registerExportRequest(@PathVariable String projectId) {
        log.info("[{}] Request to run export.", projectId);
        return projectExportService.registerRequest(projectId);
    }

    /**
     * GET /v1/projects/{projectId}/export/{requestId}
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_EXPORT + "/{requestId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ProjectExportStatusDto getExportRequestStatus(@PathVariable String projectId, @PathVariable String requestId) {
        log.info("[{}] Request to retrieve export status: {}", projectId, requestId);
        ProjectExportRequest projectExportRequest = projectExportService.getExportStatus(projectId, requestId);
        return new ProjectExportStatusDto(projectExportRequest.getRequestId(), projectExportRequest.getStatus());
    }

    /**
     * GET /v1/projects/{projectId}/export/{requestId}/download
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_EXPORT + "/{requestId}" + CurationConstants.API_DOWNLOAD,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public HttpEntity<byte[]> downloadExport(@PathVariable String projectId, @PathVariable String requestId) {
        log.info("[{}] Request to download export: {}", projectId, requestId);

        byte[] payload = projectExportService.getExportContent(projectId, requestId);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + projectId + ".zip");
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        responseHeaders.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(payload.length));
        return new HttpEntity<>(payload, responseHeaders);
    }
}

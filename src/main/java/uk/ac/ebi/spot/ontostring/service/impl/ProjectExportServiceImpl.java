package uk.ac.ebi.spot.ontostring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.constants.ProjectExportRequestStatus;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.ProjectExportRequest;
import uk.ac.ebi.spot.ontostring.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontostring.repository.ProjectExportRequestRepository;
import uk.ac.ebi.spot.ontostring.repository.ProjectRepository;
import uk.ac.ebi.spot.ontostring.service.ExportExecutorService;
import uk.ac.ebi.spot.ontostring.service.ExportFileStorageService;
import uk.ac.ebi.spot.ontostring.service.ProjectExportService;

import java.util.Optional;
import java.util.UUID;

@Service
public class ProjectExportServiceImpl implements ProjectExportService {

    private static final Logger log = LoggerFactory.getLogger(ProjectExportService.class);

    @Autowired
    private ProjectExportRequestRepository projectExportRequestRepository;

    @Autowired
    private ExportExecutorService exportExecutorService;

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private ExportFileStorageService exportFileStorageService;

    @Override
    public String registerRequest(String projectId) {
        log.info("Received export request for project: {}", projectId);
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            log.error("Project {} not found.", projectId);
            throw new EntityNotFoundException("Project " + projectId + " not found.");
        }

        ProjectExportRequest projectExportRequest = projectExportRequestRepository.insert(new ProjectExportRequest(null,
                projectId, UUID.randomUUID().toString(), ProjectExportRequestStatus.REQUESTED.name(), null));
        log.info("[{}] Export request registered: {} | {}", projectId, projectExportRequest.getId(), projectExportRequest.getRequestId());
        exportExecutorService.addToQueue(projectExportRequest);
        return projectExportRequest.getRequestId();
    }

    @Override
    public ProjectExportRequest getExportStatus(String projectId, String requestId) {
        log.info("[{}] Received status update request: {}", projectId, requestId);
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            log.error("Project {} not found.", projectId);
            throw new EntityNotFoundException("Project " + projectId + " not found.");
        }

        Optional<ProjectExportRequest> projectExportRequestOptional = projectExportRequestRepository.findByRequestId(requestId);
        if (!projectExportRequestOptional.isPresent()) {
            log.error("Project export request {} not found.", requestId);
            throw new EntityNotFoundException("Project export request " + requestId + " not found.");
        }

        return projectExportRequestOptional.get();
    }

    @Override
    public byte[] getExportContent(String projectId, String requestId) {
        log.info("[{}] Downloading content for: {}", projectId, requestId);
        Optional<Project> projectOptional = projectRepository.findById(projectId);
        if (!projectOptional.isPresent()) {
            log.error("Project {} not found.", projectId);
            throw new EntityNotFoundException("Project " + projectId + " not found.");
        }

        Optional<ProjectExportRequest> projectExportRequestOptional = projectExportRequestRepository.findByRequestId(requestId);
        if (!projectExportRequestOptional.isPresent()) {
            log.error("Project export request {} not found.", requestId);
            throw new EntityNotFoundException("Project export request " + requestId + " not found.");
        }

        return exportFileStorageService.retrieveFileContent(projectExportRequestOptional.get().getFileId());
    }
}

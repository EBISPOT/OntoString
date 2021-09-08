package uk.ac.ebi.spot.ontostring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.constants.ProjectExportRequestStatus;
import uk.ac.ebi.spot.ontostring.domain.ProjectExportRequest;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontostring.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontostring.repository.ProjectExportRequestRepository;
import uk.ac.ebi.spot.ontostring.service.*;
import uk.ac.ebi.spot.ontostring.service.*;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Optional;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Stream;

@Service
public class ExportExecutorServiceImpl implements ExportExecutorService {

    private static final Logger log = LoggerFactory.getLogger(ExportExecutorService.class);

    @Autowired
    private ProjectExportRequestRepository projectExportRequestRepository;

    @Autowired
    private ExportFileStorageService exportFileStorageService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private MappingSuggestionsService mappingSuggestionsService;

    private Queue<String> requestQueue;

    private String currentRequest;

    @PostConstruct
    public void initialize() {
        this.currentRequest = null;
        requestQueue = new ConcurrentLinkedQueue<>();
        List<ProjectExportRequest> projectExportRequests = projectExportRequestRepository.findAll();
        for (ProjectExportRequest projectExportRequest : projectExportRequests) {
            if (!projectExportRequest.getStatus().equalsIgnoreCase(ProjectExportRequestStatus.FINALIZED.name())) {
                requestQueue.add(projectExportRequest.getId());
            }
        }
        this.execute();
    }

    @Override
    @Async(value = "applicationTaskExecutor")
    public void addToQueue(ProjectExportRequest projectExportRequest) {
        log.info("Adding request to queue: {} | {}", projectExportRequest.getProjectId(), projectExportRequest.getRequestId());
        requestQueue.add(projectExportRequest.getId());
        this.execute();
    }

    private void execute() {
        if (currentRequest != null) {
            return;
        }
        if (requestQueue.isEmpty()) {
            return;
        }

        String projectRequestId = requestQueue.remove();
        log.info("Selected next item in the queue: {}", projectRequestId);
        Optional<ProjectExportRequest> projectExportRequestOptional = projectExportRequestRepository.findById(projectRequestId);
        if (!projectExportRequestOptional.isPresent()) {
            log.error("Project export request [{}] not found!", projectRequestId);
            this.execute();
        } else {
            currentRequest = projectRequestId;
            ProjectExportRequest projectExportRequest = projectExportRequestOptional.get();
            projectExportRequest.setStatus(ProjectExportRequestStatus.IN_PROGRESS.name());
            projectExportRequest = projectExportRequestRepository.save(projectExportRequest);
            this.processRequest(projectExportRequest);
            currentRequest = null;
            this.execute();
        }
    }

    private void processRequest(ProjectExportRequest projectExportRequest) {
        log.info("Processing export request: {}", projectExportRequest.getId());
        double sTime = System.currentTimeMillis();

        log.info("Export request [{}] - collecting data ...", projectExportRequest.getId());
        EntityDataCollector entityDataCollector = new EntityDataCollector(projectExportRequest.getProjectId());
        Stream<Entity> entityStream = entityService.streamEntitiesForProject(projectExportRequest.getProjectId());
        entityStream.forEach(entity -> processEntity(entity, entityDataCollector));
        entityStream.close();

        log.info("Export request [{}] - saving file ...", projectExportRequest.getId());
        ByteArrayOutputStream baos = entityDataCollector.serialize();
        if (baos == null) {
            projectExportRequest.setStatus(ProjectExportRequestStatus.FAILED.name());
            projectExportRequest = projectExportRequestRepository.save(projectExportRequest);
            double eTime = System.currentTimeMillis();
            log.info("Export request [{}] failed in: {}", projectExportRequest.getId(), (eTime - sTime) / 1000);
        }

        String fileId = exportFileStorageService.storeFile(new ByteArrayInputStream(baos.toByteArray()), projectExportRequest.getProjectId() + ".zip");
        if (fileId == null) {
            projectExportRequest.setStatus(ProjectExportRequestStatus.FAILED.name());
            projectExportRequest = projectExportRequestRepository.save(projectExportRequest);
            double eTime = System.currentTimeMillis();
            log.info("Export request [{}] failed in: {}", projectExportRequest.getId(), (eTime - sTime) / 1000);
        }

        log.info("Export request [{}] - changing status to FINALIZED.", projectExportRequest.getId());
        projectExportRequest.setFileId(fileId);
        projectExportRequest.setStatus(ProjectExportRequestStatus.FINALIZED.name());
        projectExportRequest = projectExportRequestRepository.save(projectExportRequest);
        double eTime = System.currentTimeMillis();
        log.info("Export request [{}] finalized in: {}", projectExportRequest.getId(), (eTime - sTime) / 1000);
    }

    private void processEntity(Entity entity, EntityDataCollector entityDataCollector) {
        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionsService.retrieveMappingSuggestionsForEntity(entity);
        entityDataCollector.add(entity, mapping, mappingSuggestions);
    }
}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.lang3.tuple.Pair;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.*;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataElementDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataPackageDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import javax.annotation.PostConstruct;
import java.io.IOException;

@Service
public class DataImportServiceImpl implements DataImportService {

    private static final Logger log = LoggerFactory.getLogger(DataImportService.class);

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private MatchmakerService matchmakerService;

    @Autowired
    private ImportLogService importLogService;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        this.objectMapper = new ObjectMapper();
    }

    @Async(value = "applicationTaskExecutor")
    @Override
    public void importData(String fileData, String projectId, Source source, User user) {
        log.info("[{} | {}] Importing data from file.", projectId, source.getId());
        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        Project project = projectService.retrieveProject(projectId, user);
        String batchId = importLogService.createBatch(projectId, source.getId());
        try {
            ImportDataPackageDto importDataPackageDto = this.objectMapper.readValue(fileData, new TypeReference<>() {
            });

            log.info("Received {} entries to import.", importDataPackageDto.getData().size());
            long sTime = System.currentTimeMillis();
            log.info("Creating entities ...");
            int count = 0;
            int successful = 0;
            for (ImportDataElementDto importDataElementDto : importDataPackageDto.getData()) {
                count++;
                String context = CurationConstants.CONTEXT_DEFAULT;
                if (importDataElementDto.getContext() != null) {
                    Pair<ProjectContext, Boolean> projectContextInfo = CurationUtil.findContext(importDataElementDto.getContext(), project);
                    if (!projectContextInfo.getRight()) {
                        importLogService.logEntry(new FailedImportLogEntry(null, batchId, importDataElementDto.getText(), importDataElementDto.getContext()));
                        continue;
                    }
                    context = projectContextInfo.getLeft().getName();
                }

                entityService.createEntity(new Entity(null, importDataElementDto.getText(),
                        importDataElementDto.getUpstreamId(), context,
                        source.getId(), projectId, importDataElementDto.getPriority(), provenance, EntityStatus.UNMAPPED));
                successful++;
                if (count % 100 == 0) {
                    log.info(" -- [{} | {}] Progress: {} of {}", projectId, source.getId(), count, importDataPackageDto.getData().size());
                }
            }
            long eTime = System.currentTimeMillis();
            long tTime = (eTime - sTime) / 1000;
            log.info("{} entities created [{}s]", count, tTime);
            importLogService.updateBatch(batchId, (int) tTime, count, successful);
            matchmakerService.runMatchmaking(source.getId(), project);
        } catch (IOException e) {
            log.error("Unable to deserialize import data file: {}", e.getMessage(), e);
        }
    }
}

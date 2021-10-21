package uk.ac.ebi.spot.ontostring.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.rest.controller.PlatformAdminController;
import uk.ac.ebi.spot.ontostring.service.ProjectAdminService;
import uk.ac.ebi.spot.ontostring.tasks.MappingUpdateTask;

@Service
@ConditionalOnProperty(name = "ontotools.zooma.update-schedule.enabled", havingValue = "true")
public class ProjectAdminServiceImpl implements ProjectAdminService {

    @Autowired(required = false)
    private MappingUpdateTask mappingUpdateTask;

    private static final Logger log = LoggerFactory.getLogger(PlatformAdminController.class);

    @Override
    @Async(value = "applicationTaskExecutor")
    public void runMatchmaking() {
        if (mappingUpdateTask != null) {
            log.warn("The MappingUpdateTask was not null; calling updateMappings");
            mappingUpdateTask.updateMappings();
        } else {
            log.warn("The MappingUpdateTask was null; updateMappings will not be called");
        }
    }
}

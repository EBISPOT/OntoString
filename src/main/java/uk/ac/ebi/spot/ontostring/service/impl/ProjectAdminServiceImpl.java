package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectAdminService;
import uk.ac.ebi.spot.ontotools.curation.tasks.MappingUpdateTask;

@Service
@ConditionalOnProperty(name = "ontotools.zooma.update-schedule.enabled", havingValue = "true")
public class ProjectAdminServiceImpl implements ProjectAdminService {

    @Autowired(required = false)
    private MappingUpdateTask mappingUpdateTask;

    @Override
    @Async(value = "applicationTaskExecutor")
    public void runMatchmaking() {
        if (mappingUpdateTask != null) {
            mappingUpdateTask.updateMappings();
        }
    }
}

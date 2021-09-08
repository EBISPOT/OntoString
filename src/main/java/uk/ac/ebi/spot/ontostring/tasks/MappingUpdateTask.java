package uk.ac.ebi.spot.ontostring.tasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.ProjectContext;
import uk.ac.ebi.spot.ontostring.domain.UpdateTask;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.service.MatchmakerService;
import uk.ac.ebi.spot.ontostring.service.MatchmakingLogService;
import uk.ac.ebi.spot.ontostring.service.UserService;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.constants.UpdateTaskType;
import uk.ac.ebi.spot.ontostring.repository.EntityRepository;
import uk.ac.ebi.spot.ontostring.repository.ProjectContextRepository;
import uk.ac.ebi.spot.ontostring.repository.ProjectRepository;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "ontotools.zooma.update-schedule.enabled", havingValue = "true")
public class MappingUpdateTask {

    private static final Logger log = LoggerFactory.getLogger(MappingUpdateTask.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    private ProjectContextRepository projectContextRepository;

    @Autowired
    private MatchmakingLogService matchmakingLogService;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchmakerService matchmakerService;

    @Autowired
    private UpdateTaskManager updateTaskManager;

    @Scheduled(cron = "${ontotools.zooma.update-schedule.pattern}")
    public void updateMappings() {
        log.info("Running mappings update ...");
        UpdateTask updateTask = updateTaskManager.checkAndCreateIfNecessary(UpdateTaskType.MAPPING_UPDATE.name());
        if (updateTask == null) {
            return;
        }

        double sTime = System.currentTimeMillis();
        User robotUser = userService.retrieveRobotUser();

        List<Project> projectList = projectRepository.findAll();
        int count = 1;
        for (Project project : projectList) {
            log.info("Updating project [{} of {}]: {}", count, projectList.size(), project.getName());
            List<ProjectContext> projectContexts = projectContextRepository.findByProjectId(project.getId());

            String batchId = matchmakingLogService.createBatch(project.getId());
            project.setContexts(projectContexts);

            Stream<Entity> entityStream = entityRepository.readByProjectIdAndMappingStatusIn(project.getId(),
                    Arrays.asList(new EntityStatus[]{EntityStatus.SUGGESTIONS_PROVIDED, EntityStatus.UNMAPPED}));
            entityStream.forEach(entity -> matchmakerService.autoMap(entity, project, robotUser, batchId));
            entityStream.close();
            count++;
        }

        double eTime = System.currentTimeMillis();
        double tTime = (eTime - sTime) / 1000;
        log.info("Mappings update finalized in {}s.", tTime);
        updateTaskManager.removeUpdateTask(updateTask);
    }

}

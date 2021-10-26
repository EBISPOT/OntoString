package uk.ac.ebi.spot.ontostring.tasks;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontostring.domain.UpdateTask;
import uk.ac.ebi.spot.ontostring.constants.UpdateTaskType;
import uk.ac.ebi.spot.ontostring.repository.UpdateTaskRepository;

import java.util.List;
import java.util.Optional;

@Component
public class UpdateTaskManager {

    private static final Logger log = LoggerFactory.getLogger(UpdateTaskManager.class);

    @Autowired
    private UpdateTaskRepository updateTaskRepository;

    public UpdateTask checkAndCreateIfNecessary(String updateTaskType) {
        List<UpdateTask> updateTasks = updateTaskRepository.findByTaskType(updateTaskType);
        if (!updateTasks.isEmpty()) {
            log.info("[{}] There is already an update task in the repository, which means some other node is in the process of executing the update", updateTaskType);
            if (updateTasks.size() > 1) {
                log.warn("There are more than 1 update tasks active for [{}]: {}", updateTaskType, updateTasks.size());
                for (UpdateTask updateTask : updateTasks) {
                    updateTaskRepository.delete(updateTask);
                }
            }
            return null;
        }
        UpdateTask updateTask = updateTaskRepository.insert(new UpdateTask(null, UpdateTaskType.MAPPING_UPDATE.name(), DateTime.now()));
        return updateTask;
    }

    public void removeUpdateTask(UpdateTask updateTask) {
        Optional<UpdateTask> updateTaskOptional = updateTaskRepository.findById(updateTask.getId());
        if (!updateTaskOptional.isPresent()) {
            log.warn("Update task [{} | {}] has already been deleted.", updateTask.getId(), updateTask.getTaskType());
            return;
        }
        updateTaskRepository.delete(updateTask);
    }
}

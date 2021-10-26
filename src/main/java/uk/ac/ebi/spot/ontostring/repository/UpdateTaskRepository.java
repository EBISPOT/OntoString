package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.UpdateTask;

import java.util.List;

public interface UpdateTaskRepository extends MongoRepository<UpdateTask, String> {

    List<UpdateTask> findByTaskType(String taskType);
}

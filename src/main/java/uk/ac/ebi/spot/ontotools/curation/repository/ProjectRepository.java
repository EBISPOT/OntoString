package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;

import java.util.List;

public interface ProjectRepository extends MongoRepository<Project, String> {
    List<Project> findByIdIn(List<String> projectIds);
}

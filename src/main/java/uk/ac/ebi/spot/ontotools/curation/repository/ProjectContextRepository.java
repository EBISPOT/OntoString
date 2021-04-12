package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;

import java.util.List;
import java.util.Optional;

public interface ProjectContextRepository extends MongoRepository<ProjectContext, String> {

    List<ProjectContext> findByProjectId(String projectId);

    Optional<ProjectContext> findByProjectIdAndNameIgnoreCase(String projectId, String name);
}

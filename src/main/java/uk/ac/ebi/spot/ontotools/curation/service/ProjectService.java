package uk.ac.ebi.spot.ontotools.curation.service;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

import java.util.List;

public interface ProjectService {
    List<Project> retrieveProjects(User user);

    Project createProject(Pair<Project, ProjectContext> projectCreationPair, User user);

    Project updateProject(Project project, String projectId, User user);

    void deleteProject(String projectId, User user);

    Project retrieveProject(String projectId, User user);

    void verifyAccess(String projectId, User user, List<ProjectRole> roles);

    Project createProjectContext(ProjectContext projectContext, String projectId, User user);

    Project updateProjectContext(ProjectContext projectContext, String projectId, User user);

    void deleteProjectContext(String contextName, String projectId, User user);
}

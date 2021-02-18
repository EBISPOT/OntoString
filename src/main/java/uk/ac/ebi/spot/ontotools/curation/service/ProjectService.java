package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

import java.util.List;

public interface ProjectService {
    List<Project> retrieveProjects(User user);

    Project createProject(Project disassemble, User user);

    Project updateProject(Project disassemble, String projectId, User user);

    void deleteProject(String projectId, User user);

    Project retrieveProject(String projectId, User user);

    void verifyAccess(String projectId, User user, List<ProjectRole> roles);
}

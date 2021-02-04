package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface UserService {
    User findByEmail(String email);

    User findRandomSuperUser();

    void addProjectToUser(User user, Project project, ProjectRole role);

    void removeProjectFromUser(User user, String projectId);
}

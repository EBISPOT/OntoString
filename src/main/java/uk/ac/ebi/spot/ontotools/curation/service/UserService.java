package uk.ac.ebi.spot.ontotools.curation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

import java.util.List;

public interface UserService {
    User findByEmail(String email);

    User findRandomSuperUser();

    User retrieveRobotUser();

    User addUserToProject(User user, String projectId, List<ProjectRole> roles);

    void removeProjectFromUser(User user, String projectId);

    List<User> findByProjectId(String projectId);

    User updateUserRoles(User targetUser, String projectId, List<ProjectRole> projectRoles);

    User findById(String userId);

    Page<User> retrieveUsers(String prefix, Pageable pageable);

    User createUser(String name, String email);
}

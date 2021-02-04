package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Role;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.UserRepository;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        log.info("Retrieving user: {}", email);

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (!userOptional.isPresent()) {
            log.error("Unable to find user with email: {}", email);
            throw new EntityNotFoundException("Unable to find user with email: " + email);
        }

        return userOptional.get();
    }

    @Override
    public User findRandomSuperUser() {
        log.info("Retrieving random super user ...");
        List<User> superUsers = userRepository.findBySuperUser(true);
        if (superUsers.isEmpty()) {
            log.error("Unable to find any super users!");
            throw new EntityNotFoundException("Unable to find any super users!");
        }

        log.info("Returning user: {}", superUsers.get(0).getEmail());
        return superUsers.get(0);
    }

    @Override
    public void addProjectToUser(User user, Project project, ProjectRole role) {
        log.info("Adding project [{}] to user [{}] with role: {}", project.getId(), user.getName(), role.name());
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }
        user.getRoles().add(new Role(project.getId(), role));
        userRepository.save(user);
    }

    @Override
    public void removeProjectFromUser(User user, String projectId) {
        log.info("Removing project [{}] from user: {}", projectId, user.getName());
        if (user.getRoles() == null) {
            user.setRoles(new ArrayList<>());
        }

        Role found = null;
        for (Role role : user.getRoles()) {
            if (role.getProjectId().equals(projectId)) {
                found = role;
                break;
            }
        }
        if (found != null) {
            user.getRoles().remove(found);
            userRepository.save(user);
        } else {
            log.warn("Unable to find project [{}] associated with user: {}", projectId, user.getName());
        }
    }
}

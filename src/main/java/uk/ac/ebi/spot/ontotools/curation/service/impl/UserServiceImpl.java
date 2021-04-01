package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Role;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.UserRepository;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.SystemConfigProperties;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SystemConfigProperties systemConfigProperties;

    private User robotUser;

    @PostConstruct
    public void initialize() {
        this.robotUser = new User(null, "Robot User", systemConfigProperties.getRobotUser(), new ArrayList<>(), true);
    }

    @Override
    public User retrieveRobotUser() {
        return robotUser;
    }

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
    public User addUserToProject(User targetUser, String projectId, List<ProjectRole> roles) {
        User existing = this.findByEmail(targetUser.getEmail());
        log.info("Adding project [{}] to user [{}] with roles: {}", projectId, existing.getName(), roles);
        if (existing.getRoles() == null) {
            existing.setRoles(new ArrayList<>());
        }
        List<ProjectRole> existingRoles = new ArrayList<>();
        List<Role> toRemove = new ArrayList<>();
        for (Role role : existing.getRoles()) {
            if (role.getProjectId().equalsIgnoreCase(projectId)) {
                existingRoles.add(role.getRole());
                toRemove.add(role);
            }
        }
        for (Role role : toRemove) {
            existing.getRoles().remove(role);
        }
        for (ProjectRole projectRole : roles) {
            if (!existingRoles.contains(projectRole)) {
                existingRoles.add(projectRole);
            }
        }

        for (ProjectRole projectRole : existingRoles) {
            existing.getRoles().add(new Role(projectId, projectRole));
        }
        return userRepository.save(existing);
    }

    @Override
    public User updateUserRoles(User targetUser, String projectId, List<ProjectRole> projectRoles) {
        User existing = this.findByEmail(targetUser.getEmail());
        log.info("Updating roles for user [{}] in project: {}", existing.getEmail(), projectId);
        List<Role> toRemove = new ArrayList<>();
        for (Role role : existing.getRoles()) {
            if (role.getProjectId().equalsIgnoreCase(projectId)) {
                toRemove.add(role);
            }
        }
        for (Role role : toRemove) {
            existing.getRoles().remove(role);
        }

        for (ProjectRole projectRole : projectRoles) {
            existing.getRoles().add(new Role(projectId, projectRole));
        }
        return userRepository.save(existing);
    }

    @Override
    public User findById(String userId) {
        log.info("Retrieving user: {}", userId);

        Optional<User> userOptional = userRepository.findById(userId);
        if (!userOptional.isPresent()) {
            log.error("Unable to find user: {}", userId);
            throw new EntityNotFoundException("Unable to find user: " + userId);
        }

        return userOptional.get();
    }

    @Override
    public Page<User> retrieveUsers(String prefix, Pageable page) {
        log.info("Retrieving users: {}", prefix);
        Page<User> userPage = prefix == null ? userRepository.findAll(page) : userRepository.findByNameLikeIgnoreCase(prefix, page);
        return userPage;
    }

    @Override
    public User createUser(String name, String email) {
        log.info("Creating user: {} | {}", name, email);
        User created = userRepository.insert(new User(null, name, email, new ArrayList<>(), false));
        log.info("User [{}] created: {}", email, created.getId());
        return created;
    }

    @Override
    public void removeProjectFromUser(User targetUser, String projectId) {
        User existing = this.findByEmail(targetUser.getEmail());
        log.info("Removing project [{}] from user: {}", projectId, existing.getName());
        if (existing.getRoles() == null) {
            existing.setRoles(new ArrayList<>());
        }

        Role found = null;
        for (Role role : existing.getRoles()) {
            if (role.getProjectId().equals(projectId)) {
                found = role;
                break;
            }
        }
        if (found != null) {
            existing.getRoles().remove(found);
            userRepository.save(existing);
        } else {
            log.warn("Unable to find project [{}] associated with user: {}", projectId, existing.getName());
        }
    }

    @Override
    public List<User> findByProjectId(String projectId) {
        log.info("Retrieving users for project: {}", projectId);
        List<User> users = userRepository.findByRoles_ProjectId(projectId);
        log.info("Found {} users.", users.size());
        return users;
    }

}

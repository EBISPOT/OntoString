package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Role;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.AuthorizationException;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.ProjectRepository;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Project> retrieveProjects(User user) {
        log.info("Retrieving projects for user: {}", user.getEmail());
        List<String> projectIds = user.getRoles().stream().map(Role::getProjectId).collect(Collectors.toList());
        List<Project> projects = user.isSuperUser() ? projectRepository.findAll() : projectRepository.findByIdIn(projectIds);
        log.info("Found {} projects: ", projects.size());
        return projects;
    }

    @Override
    public Project createProject(Project toCreate, User user) {
        log.info("[{}] Creating project: {}", user.getEmail(), toCreate.getName());
        Project created = projectRepository.insert(toCreate);
        log.info("[{}] Project created: {}", created.getName(), created.getId());
        return created;
    }

    @Override
    public Project updateProject(Project project, String projectId, User user) {
        log.info("[{}] Updating project: {}", user.getEmail(), projectId);
        Optional<Project> exitingOp = projectRepository.findById(projectId);
        if (!exitingOp.isPresent()) {
            log.error("[{}] Unable to find project: {}", user.getEmail(), projectId);
            throw new EntityNotFoundException("[" + user.getEmail() + "] Unable to find project: " + projectId);
        }
        if (hasAccess(user, projectId, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN}))) {
            Project existing = exitingOp.get();
            existing.setDatasources(project.getDatasources() != null ? project.getDatasources() : new ArrayList<>());
            existing.setOntologies(project.getOntologies() != null ? project.getOntologies() : new ArrayList<>());
            existing.setName(project.getName());
            existing.setDescription(project.getDescription());
            return projectRepository.save(existing);
        } else {
            log.error("User [{}] cannot change project [{}]. Required access is missing.", user.getEmail(), projectId);
            throw new AuthorizationException("User [" + user.getEmail() + "] cannot change project [" + projectId + "]. Required access is missing.");
        }
    }

    @Override
    public void deleteProject(String projectId, User user) {
        log.info("[{}] Deleting project: {}", user.getEmail(), projectId);
        Optional<Project> exitingOp = projectRepository.findById(projectId);
        if (!exitingOp.isPresent()) {
            log.error("[{}] Unable to find project: {}", user.getEmail(), projectId);
            throw new EntityNotFoundException("[" + user.getEmail() + "] Unable to find project: " + projectId);
        }
        if (hasAccess(user, projectId, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN}))) {
            Project existing = exitingOp.get();
            projectRepository.delete(existing);

            /**
             * TODO: Implement downstream consequences
             */
        } else {
            log.error("User [{}] cannot delete project [{}]. Required access is missing.", user.getEmail(), projectId);
            throw new AuthorizationException("User [" + user.getEmail() + "] cannot delete project [" + projectId + "]. Required access is missing.");
        }
    }

    @Override
    public Project retrieveProject(String projectId, User user) {
        log.info("[{}] Retrieving project: {}", user.getEmail(), projectId);
        if (!hasAccess(user, projectId, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONSUMER, ProjectRole.CONTRIBUTOR}))) {
            log.error("[{}] User does not have access project: {}", user.getEmail(), projectId);
            throw new EntityNotFoundException("[" + user.getEmail() + "] Unable to find project: " + projectId);
        }

        Optional<Project> exitingOp = projectRepository.findById(projectId);
        if (!exitingOp.isPresent()) {
            log.error("[{}] Unable to find project: {}", user.getEmail(), projectId);
            throw new EntityNotFoundException("[" + user.getEmail() + "] Unable to find project: " + projectId);
        }

        return exitingOp.get();
    }

    @Override
    public void verifyAccess(String projectId, User user, List<ProjectRole> roles) {
        log.info("[{}] Verifying access to project: {}", user.getEmail(), projectId);
        if (!hasAccess(user, projectId, roles)) {
            log.error("[{}] User does not have access project: {}", user.getEmail(), projectId);
            throw new EntityNotFoundException("[" + user.getEmail() + "] Unable to find project: " + projectId);
        }
        Optional<Project> exitingOp = projectRepository.findById(projectId);
        if (!exitingOp.isPresent()) {
            log.error("[{}] Unable to find project: {}", user.getEmail(), projectId);
            throw new EntityNotFoundException("[" + user.getEmail() + "] Unable to find project: " + projectId);
        }
    }

    private boolean hasAccess(User user, String projectId, List<ProjectRole> roles) {
        if (user.getRoles() != null) {
            for (Role role : user.getRoles()) {
                if (role.getProjectId().equals(projectId) && roles.contains(role.getRole())) {
                    return true;
                }
            }
        }

        return false;
    }
}

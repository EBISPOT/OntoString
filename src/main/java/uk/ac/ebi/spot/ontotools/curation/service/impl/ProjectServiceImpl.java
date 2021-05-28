package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Role;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.ProjectContextRepository;
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

    @Autowired
    private ProjectContextRepository projectContextRepository;

    @Override
    public List<Project> retrieveProjects(User user) {
        log.info("Retrieving projects for user: {}", user.getEmail());
        List<String> projectIds = user.getRoles().stream().map(Role::getProjectId).collect(Collectors.toList());
        List<Project> projects = user.isSuperUser() ? projectRepository.findAll() : projectRepository.findByIdIn(projectIds);
        List<Project> result = new ArrayList<>();
        for (Project project : projects) {
            List<ProjectContext> projectContexts = projectContextRepository.findByProjectId(project.getId());
            project.setContexts(projectContexts);
            result.add(project);
        }
        log.info("Found {} projects: ", projects.size());
        return result;
    }

    @Override
    public Project createProject(Pair<Project, ProjectContext> projectCreationPair, User user) {
        log.info("[{}] Creating project: {}", user.getEmail(), projectCreationPair.getLeft().getName());
        Project created = projectRepository.insert(projectCreationPair.getLeft());
        ProjectContext projectContext = projectCreationPair.getRight();
        projectContext.setProjectId(created.getId());
        projectContext = projectContextRepository.insert(projectContext);
        created.setContextIds(Arrays.asList(new String[]{projectContext.getId()}));
        created = projectRepository.save(created);
        log.info("[{}] Project created: {}", created.getName(), created.getId());
        created.setContexts(Arrays.asList(new ProjectContext[]{projectContext}));
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
            existing.setName(project.getName());
            existing.setDescription(project.getDescription());
            existing.setNumberOfReviewsRequired(project.getNumberOfReviewsRequired());
            existing = projectRepository.save(existing);

            List<ProjectContext> projectContexts = projectContextRepository.findByProjectId(existing.getId());
            existing.setContexts(projectContexts);
            return existing;
        } else {
            log.error("User [{}] cannot change project [{}]. Required access is missing.", user.getEmail(), projectId);
            throw new EntityNotFoundException("No project [" + projectId + "] found for user [" + user.getEmail() + "]");
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
            throw new EntityNotFoundException("No project [" + projectId + "] found for user [" + user.getEmail() + "]");
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
        Project project = exitingOp.get();
        List<ProjectContext> projectContexts = projectContextRepository.findByProjectId(project.getId());
        project.setContexts(projectContexts);
        return project;
    }

    @Override
    public Project createProjectContext(ProjectContext projectContext, String projectId, User user) {
        log.info("[{}] Creating project context [{}]: {}", user.getEmail(), projectId, projectContext.getName());
        Project project = this.retrieveProject(projectId, user);
        projectContext.setProjectId(project.getId());
        projectContext = projectContextRepository.insert(projectContext);
        project.getContextIds().add(projectContext.getId());
        project = projectRepository.save(project);
        List<ProjectContext> projectContexts = projectContextRepository.findByProjectId(project.getId());
        project.setContexts(projectContexts);
        return project;
    }

    @Override
    public Project updateProjectContext(ProjectContext projectContext, String projectId, User user) {
        log.info("[{}] Updating project context [{}]: {}", user.getEmail(), projectId, projectContext.getName());
        Project project = this.retrieveProject(projectId, user);
        Optional<ProjectContext> projectContextOptional = projectContextRepository.findByProjectIdAndNameIgnoreCase(projectId, projectContext.getName());
        if (!projectContextOptional.isPresent()) {
            log.error("Project context [{}] not found for project {}.", projectContext.getName(), project.getName());
            throw new EntityNotFoundException("Project context [" + projectContext.getName() + "] not found for project " + project.getName() + ".");
        }
        ProjectContext existing = projectContextOptional.get();
        existing.setDescription(projectContext.getDescription());
        existing.setDatasources(projectContext.getDatasources());
        existing.setOntologies(projectContext.getOntologies());
        existing.setPreferredMappingOntologies(projectContext.getPreferredMappingOntologies());
        existing.setProjectContextGraphRestriction(projectContext.getProjectContextGraphRestriction());
        projectContextRepository.save(existing);
        List<ProjectContext> projectContexts = projectContextRepository.findByProjectId(project.getId());
        project.setContexts(projectContexts);
        return project;
    }

    @Override
    public void deleteProjectContext(String contextName, String projectId, User user) {
        log.info("[{}] Deleting project context [{}]: {}", user.getEmail(), projectId, contextName);
        Project project = this.retrieveProject(projectId, user);
        Optional<ProjectContext> projectContextOptional = projectContextRepository.findByProjectIdAndNameIgnoreCase(projectId, contextName);
        if (!projectContextOptional.isPresent()) {
            log.error("Project context [{}] not found for project {}.", contextName, project.getName());
            throw new EntityNotFoundException("Project context [" + contextName + "] not found for project " + project.getName() + ".");
        }

        ProjectContext existing = projectContextOptional.get();
        project.getContextIds().remove(existing.getId());
        projectRepository.save(project);
        projectContextRepository.delete(existing);
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
        if (user.isSuperUser()) {
            return true;
        }
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

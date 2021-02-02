package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Role;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.repository.ProjectRepository;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProjectServiceImpl implements ProjectService {

    private static final Logger log = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    private ProjectRepository projectRepository;

    @Override
    public List<Project> retrieveProjects(User user) {
        log.info("Retrieving projects for user: {}", user.getEmail());
        List<String> projectIds = user.getRoles().stream().map(Role::getProject).collect(Collectors.toList());
        List<Project> projects = user.isSuperUser() ? projectRepository.findAll() : projectRepository.findByIdIn(projectIds);
        log.info("Found {} projects: ", projects.size());
        return projects;
    }
}

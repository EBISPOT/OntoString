package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

import java.util.List;

public interface ProjectService {
    List<Project> retrieveProjects(User user);
}

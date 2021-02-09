package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;

public interface MatchmakerService {

    void runMatchmaking(String sourceId, Project project);
}

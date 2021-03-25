package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;

public interface MatchmakerService {

    void runMatchmaking(String sourceId, Project project);

    void autoMap(Entity entity, Project project, User user, String batchId);
}

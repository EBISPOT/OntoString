package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;

public interface MatchmakerService {

    void runMatchmaking(String sourceId, Project project);

    void autoMap(Entity entity, Project project, User user, String batchId);
}

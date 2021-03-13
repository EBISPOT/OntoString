package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;

public interface MatchmakerService {

    void runMatchmaking(Source source, Project project);
}

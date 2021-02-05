package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;

public interface MappingService {

    void runAutoMapping(String sourceId, Project project);
}

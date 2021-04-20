package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface OntologyTermUtilService {
    void actionTerms(String projectId, String context, String status, String comment, User user);
}

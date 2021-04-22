package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Map;

public interface OntologyTermUtilService {
    void actionTerms(String projectId, String context, String status, String comment, User user);

    String exportOntologyTerms(String projectId, String context, String status);

    Map<String, Map<String, String>> retrieveEntityData(List<OntologyTerm> ontologyTerms, String projectId, String context);
}

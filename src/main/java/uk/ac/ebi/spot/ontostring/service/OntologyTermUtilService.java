package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTermContext;

import java.util.List;
import java.util.Map;

public interface OntologyTermUtilService {
    void actionTerms(String projectId, String context, String status, String comment, User user);

    String exportOntologyTerms(String projectId, String context, String status);

    Map<OntologyTerm, Map<String, String>> retrieveEntityData(List<String> ontoTermIds, List<OntologyTermContext> ontologyTermContexts, String projectId, String context);
}

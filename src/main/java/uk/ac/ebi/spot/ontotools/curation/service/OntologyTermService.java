package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;

import java.util.List;
import java.util.Map;

public interface OntologyTermService {
    OntologyTerm createTerm(String iri, Project project);

    Map<String, OntologyTerm> retrieveTerms(List<String> ontologyTermIds);

    List<OntologyTerm> retrieveAllTerms();
}

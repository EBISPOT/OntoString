package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;

import java.util.List;
import java.util.Map;

public interface OntologyTermService {
    OntologyTerm createTerm(OntologyTerm term);

    OntologyTerm createTerm(String iri, Project project);

    Map<String, OntologyTerm> retrieveTerms(List<String> ontologyTermIds);

    List<OntologyTerm> retrieveAllTerms();

    OntologyTerm retrieveTermByIri(String iri);

    OntologyTerm retrieveTermById(String ontologyTermId);
}

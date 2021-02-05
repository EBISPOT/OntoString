package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;

import java.util.List;
import java.util.Map;

public interface OntologyTermService {
    Map<String, OntologyTerm> getOntologyTermsById(List<String> ontoTermIds);

    OntologyTerm createTerm(String iri);
}

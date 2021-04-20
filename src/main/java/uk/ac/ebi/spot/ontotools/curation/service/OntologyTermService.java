package uk.ac.ebi.spot.ontotools.curation.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Map;

public interface OntologyTermService {
    OntologyTerm createTerm(OntologyTerm term);

    OntologyTerm createTerm(String iri, ProjectContext projectContext);

    Map<String, OntologyTerm> retrieveTerms(List<String> ontologyTermIds);

    List<OntologyTerm> retrieveAllTerms();

    OntologyTerm retrieveTermByCurie(String curie);

    OntologyTerm retrieveTermById(String ontologyTermId);

    List<OntologyTerm> retrieveTermByCuries(List<String> curies);

    String retrieveStatusUpdate(String iri, ProjectContext projectContext, String previousStatus);

    Page<OntologyTerm> retrieveTermsByStatus(String projectId, String context, String status, Pageable pageable);
}

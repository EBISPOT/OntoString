package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;

import java.util.List;

public interface MappingSuggestionsService {

    void createMappingSuggestion(Entity entity, OntologyTerm ontologyTerm);

    void deleteMappingSuggestionsExcluding(Entity entity, List<OntologyTerm> ontologyTerms);
}

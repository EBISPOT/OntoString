package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

import java.util.List;
import java.util.Map;

public interface MappingSuggestionsService {

    MappingSuggestion createMappingSuggestion(Entity entity, OntologyTerm ontologyTerm, Provenance provenance);

    void deleteMappingSuggestionsExcluding(Entity entity, List<OntologyTerm> ontologyTerms);

    Map<String, List<MappingSuggestion>> retrieveMappingSuggestionsForEntities(List<String> entityIds);

    List<MappingSuggestion> retrieveMappingSuggestionsForEntity(String entityId);

    void deleteMappingSuggestions(String entityId, String ontologyTermId);
}

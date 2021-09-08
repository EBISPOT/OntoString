package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontostring.domain.Provenance;

import java.util.List;
import java.util.Map;

public interface MappingSuggestionsService {

    MappingSuggestion createMappingSuggestion(Entity entity, OntologyTerm ontologyTerm, Provenance provenance);

    void deleteMappingSuggestionsExcluding(Entity entity, List<OntologyTerm> ontologyTerms, Provenance provenance);

    Map<String, List<MappingSuggestion>> retrieveMappingSuggestionsForEntities(List<String> entityIds, String projectId, String context);

    List<MappingSuggestion> retrieveMappingSuggestionsForEntity(Entity entity);

    void deleteMappingSuggestions(String entityId, OntologyTerm ontologyTerm, Provenance provenance);
}

package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;

import java.util.List;
import java.util.Optional;

public interface MappingSuggestionRepository extends MongoRepository<MappingSuggestion, String> {

    Optional<MappingSuggestion> findByEntityIdAndOntologyTermId(String entityId, String ontologyTermId);

    List<MappingSuggestion> findByEntityIdIn(List<String> entityIds);

    List<MappingSuggestion> findByEntityIdAndOntologyTermIdNotIn(String entityId, List<String> ontologyTermIds);

    List<MappingSuggestion> findByEntityId(String entityId);
}

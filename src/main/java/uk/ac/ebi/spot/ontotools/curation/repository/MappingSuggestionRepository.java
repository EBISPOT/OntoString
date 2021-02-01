package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.MappingSuggestion;

public interface MappingSuggestionRepository extends MongoRepository<MappingSuggestion, String> {
}

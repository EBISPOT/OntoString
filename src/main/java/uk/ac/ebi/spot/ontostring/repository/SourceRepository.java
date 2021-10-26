package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.Source;

import java.util.List;
import java.util.Optional;

public interface SourceRepository extends MongoRepository<Source, String> {

    List<Source> findByProjectIdAndArchived(String projectId, boolean archived);

    Optional<Source> findByIdAndProjectIdAndArchived(String sourceId, String projectId, boolean archived);
}

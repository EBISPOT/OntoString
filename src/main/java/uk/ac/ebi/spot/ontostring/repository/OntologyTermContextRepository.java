package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTermContext;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface OntologyTermContextRepository extends MongoRepository<OntologyTermContext, String> {
    Optional<OntologyTermContext> findByOntologyTermIdAndProjectIdAndContext(String ontoTermId, String projectId, String context);

    Page<OntologyTermContext> findByHasMappingAndProjectIdAndContextAndStatus(boolean hasMapping, String projectId, String context, String status, Pageable pageable);

    List<OntologyTermContext> findByHasMappingAndProjectIdAndContextAndStatus(boolean hasMapping, String projectId, String context, String status);

    Stream<OntologyTermContext> readByHasMappingAndProjectIdAndContextAndStatus(boolean hasMapping, String projectId, String context, String status);

    long countByHasMappingAndProjectIdAndContextAndStatus(boolean hasMapping, String projectId, String context, String status);

    List<OntologyTermContext> findByOntologyTermId(String ontoTermId);
}

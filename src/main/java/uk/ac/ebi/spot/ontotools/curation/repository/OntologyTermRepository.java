package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

public interface OntologyTermRepository extends MongoRepository<OntologyTerm, String> {
    List<OntologyTerm> findByIdIn(List<String> ontoTermIds);

    Optional<OntologyTerm> findByIriHash(String iriHash);

    Optional<OntologyTerm> findByCurie(String curie);

    List<OntologyTerm> findByCurieIn(List<String> curies);

    Page<OntologyTerm> findByContexts_ProjectIdAndContexts_ContextAndContexts_Status(String projectId, String context, String status, Pageable pageable);

    Stream<OntologyTerm> readByContexts_ProjectIdAndContexts_ContextAndContexts_Status(String projectId, String context, String status);

    @Query(value = "{}")
    Stream<OntologyTerm> findAllByCustomQueryAndStream();
}

package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;

import java.util.List;
import java.util.Optional;

public interface MappingRepository extends MongoRepository<Mapping, String> {
    List<Mapping> findByEntityIdIn(List<String> entityIds);

    Optional<Mapping> findByEntityId(String entityId);

    List<Mapping> findByOntologyTermIdsContains(String ontoTermId);

    List<Mapping> findByProjectIdAndContextAndOntologyTermIdsContains(String projectId, String context, String ontoTermId);

    List<Mapping> findByProjectId(String projectId);

    List<Mapping> findByProjectIdAndContext(String projectId, String context);

    List<Mapping> findByIdIn(List<String> mappingIds);
}

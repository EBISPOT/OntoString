package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.ProjectExportRequest;

import java.util.Optional;

public interface ProjectExportRequestRepository extends MongoRepository<ProjectExportRequest, String> {

    Optional<ProjectExportRequest> findByRequestId(String requestId);

}

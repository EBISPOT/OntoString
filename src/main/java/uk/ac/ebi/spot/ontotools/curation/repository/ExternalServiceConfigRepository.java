package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;

import java.util.Optional;

public interface ExternalServiceConfigRepository extends MongoRepository<ExternalServiceConfig, String> {
    Optional<ExternalServiceConfig> findByName(String name);
}

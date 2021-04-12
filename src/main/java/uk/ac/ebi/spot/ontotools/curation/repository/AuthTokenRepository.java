package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.AuthToken;

import java.util.Optional;

public interface AuthTokenRepository extends MongoRepository<AuthToken, String> {
    Optional<AuthToken> findByToken(String jwt);
}

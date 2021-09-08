package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.auth.User;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findBySuperUser(boolean superUser);

    List<User> findByRoles_ProjectId(String projectId);

    Page<User> findByNameLikeIgnoreCase(String prefix, Pageable pageable);

}

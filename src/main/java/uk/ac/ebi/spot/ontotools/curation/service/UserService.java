package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface UserService {
    User findByEmail(String email);

    User findRandomSuperUser();
}

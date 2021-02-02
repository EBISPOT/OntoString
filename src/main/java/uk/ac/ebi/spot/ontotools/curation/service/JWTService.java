package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface JWTService {
    User extractUser(String jwt);
}

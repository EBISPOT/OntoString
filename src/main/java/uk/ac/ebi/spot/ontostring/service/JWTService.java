package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.auth.User;

public interface JWTService {
    User extractUser(String jwt);

    User extractUserSlim(String jwt);
}

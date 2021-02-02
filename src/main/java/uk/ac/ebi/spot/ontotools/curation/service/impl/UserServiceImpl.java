package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.UserRepository;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger log = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Override
    public User findByEmail(String email) {
        log.info("Retrieving user: {}", email);

        Optional<User> userOptional = userRepository.findByEmailIgnoreCase(email);
        if (!userOptional.isPresent()) {
            log.error("Unable to find user with email: {}", email);
            throw new EntityNotFoundException("Unable to find user with email: " + email);
        }

        return userOptional.get();
    }

    @Override
    public User findRandomSuperUser() {
        log.info("Retrieving random super user ...");
        List<User> superUsers = userRepository.findBySuperUser(true);
        if (superUsers.isEmpty()) {
            log.error("Unable to find any super users!");
            throw new EntityNotFoundException("Unable to find any super users!");
        }

        log.info("Returning user: {}", superUsers.get(0).getEmail());
        return superUsers.get(0);
    }
}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.AuthToken;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.AuthenticationException;
import uk.ac.ebi.spot.ontotools.curation.repository.AuthTokenRepository;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.SystemConfigProperties;

import javax.annotation.PostConstruct;
import java.io.InputStream;
import java.security.PublicKey;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Optional;

@Service
public class JWTServiceImpl implements JWTService {

    private static final Logger log = LoggerFactory.getLogger(JWTService.class);

    @Autowired
    private SystemConfigProperties systemConfigProperties;

    @Autowired
    private UserService userService;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    private PublicKey verifyingKey;

    @PostConstruct
    public void initialize() {
        log.info("Initializing auth cert. Auth enabled: {}", systemConfigProperties.isAuthEnabled());
        if (systemConfigProperties.isAuthEnabled()) {
            String certPath = systemConfigProperties.getCertPath();
            log.info("Using cert: {}", certPath);
            if (certPath == null) {
                log.error("Unable to initialize cert. Path is NULL.");
            } else {
                try {
                    InputStream inputStream = new DefaultResourceLoader().getResource(certPath).getInputStream();
                    final CertificateFactory certificateFactory = CertificateFactory.getInstance("X.509");
                    final X509Certificate certificate = (X509Certificate) certificateFactory.generateCertificate(inputStream);
                    verifyingKey = certificate.getPublicKey();
                } catch (Exception e) {
                    log.error("Unable to initialize cert: {}", e.getMessage(), e);
                }
            }
        }
    }


    @Override
    public User extractUser(String jwt) {
        Optional<AuthToken> authTokenOptional = authTokenRepository.findByToken(jwt);
        log.info("Token is privileged: {}", authTokenOptional.isPresent());
        if (authTokenOptional.isPresent()) {
            User user = userService.findByEmail(authTokenOptional.get().getEmail());
            log.info("User found: {} - {}", user.getName(), user.getEmail());
            return user;
        }

        return extractUserSlim(jwt);
    }

    @Override
    public User extractUserSlim(String jwt) {
        log.info("Auth enabled: {}", systemConfigProperties.isAuthEnabled());
        if (systemConfigProperties.isAuthEnabled()) {
            if (jwt == null) {
                log.error("Unauthorised access. JWT missing.");
                throw new AuthenticationException("Unauthorised access. JWT missing.");
            }

            Claims jwtClaims;
            try {
                jwtClaims = Jwts.parser().setSigningKey(verifyingKey).parseClaimsJws(jwt).getBody();
            } catch (Exception e) {
                log.error("Unable to parse JWT: {}", e.getMessage(), e);
                throw new AuthenticationException("Unauthorised access: " + e.getMessage());
            }
            String userReference = jwtClaims.getSubject();
            String name = null;
            String email = null;
            if (jwtClaims.get(IDPConstants.JWT_EMAIL) != null) {
                email = (String) jwtClaims.get(IDPConstants.JWT_EMAIL);
            }
            if (jwtClaims.get(IDPConstants.JWT_NAME) != null) {
                name = (String) jwtClaims.get(IDPConstants.JWT_NAME);
            }
            if (name == null || email == null || userReference == null) {
                log.error("Unable to parse JWT: Name, email or userReference missing.");
                throw new AuthenticationException("Unauthorised access: Name, email or userReference missing.");
            }

            User user = userService.findByEmail(email);
            log.info("Found user: {} | {}", user.getName(), user.getEmail());
            return user;
        }
        return userService.findRandomSuperUser();
    }
}

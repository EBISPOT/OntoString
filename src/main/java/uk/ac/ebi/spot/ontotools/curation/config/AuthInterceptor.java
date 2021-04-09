package uk.ac.ebi.spot.ontotools.curation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.AuthToken;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.AuthenticationException;
import uk.ac.ebi.spot.ontotools.curation.repository.AuthTokenRepository;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.SystemConfigProperties;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;

public class AuthInterceptor implements HandlerInterceptor {

    private static final Logger log = LoggerFactory.getLogger(AuthInterceptor.class);

    @Autowired
    private SystemConfigProperties systemConfigProperties;

    @Autowired
    private JWTService jwtService;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    @Autowired
    private UserService userService;

    @Override
    public boolean preHandle(HttpServletRequest httpServletRequest,
                             HttpServletResponse httpServletResponse,
                             Object o) throws IOException {
        String requestedURI = httpServletRequest.getRequestURI();
        log.debug("Authentication enabled: {}", systemConfigProperties.isAuthEnabled());
        if (requestedURI.equals("/error")) {
            return false;
        }
        if ((systemConfigProperties.getUnauthenticatedEndpointsPrefix() != null &&
                requestedURI.startsWith(systemConfigProperties.getUnauthenticatedEndpointsPrefix())) ||
                (!requestedURI.startsWith("/v1"))) {
            log.debug("Received call on unauthenticated endpoint: {}", requestedURI);
            return true;
        }
        if (!systemConfigProperties.isAuthEnabled()) {
            return true;
        }

        String jwt = HeadersUtil.extractJWT(httpServletRequest);

        if (jwt == null) {
            log.error("Authorization failure. JWT token is null.");
            throw new AuthenticationException("Authorization failure. JWT token is null.");
        }
        if ("".equals(jwt)) {
            log.error("Authorization failure. JWT token is null.");
            throw new AuthenticationException("Authorization failure. JWT token is null.");
        }

        Optional<AuthToken> authTokenOptional = authTokenRepository.findByToken(jwt);
        log.debug("Token is privileged: {}", authTokenOptional.isPresent());
        if (authTokenOptional.isPresent()) {
            User user = userService.findByEmail(authTokenOptional.get().getEmail());
            log.debug("User found: {} - {}", user.getName(), user.getEmail());
            return true;
        }

        try {
            User user = jwtService.extractUserSlim(jwt);
            log.debug("User found: {} - {}", user.getName(), user.getEmail());
            return true;
        } catch (Exception e) {
            log.error("Authorization failure: {}", e.getMessage(), e);
            throw new AuthenticationException(e.getMessage());
        }
    }
}

package uk.ac.ebi.spot.ontotools.curation.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.servlet.HandlerInterceptor;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.AuthToken;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.exception.AuthorizationException;
import uk.ac.ebi.spot.ontotools.curation.repository.AuthTokenRepository;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.SystemConfigProperties;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
                             Object o) {
        if (!"/error".equals(httpServletRequest.getRequestURI())) {
            log.info("Authentication enabled: {}", systemConfigProperties.isAuthEnabled());
            if (!systemConfigProperties.isAuthEnabled()) {
                return true;
            }

            String jwt = HeadersUtil.extractJWT(httpServletRequest);

            if (jwt == null) {
                log.error("Authorization failure. JWT token is null.");
                throw new AuthorizationException("Authorization failure. JWT token is null.");
            }
            if ("".equals(jwt)) {
                log.error("Authorization failure. JWT token is null.");
                throw new AuthorizationException("Authorization failure. JWT token is null.");
            }

            Optional<AuthToken> authTokenOptional = authTokenRepository.findByToken(jwt);
            log.info("Token is privileged: {}", authTokenOptional.isPresent());
            if (authTokenOptional.isPresent()) {
                User user = userService.findByEmail(authTokenOptional.get().getEmail());
                log.info("User found: {} - {}", user.getName(), user.getEmail());
                return true;
            }

            try {
                User user = jwtService.extractUser(jwt);
                log.info("User found: {} - {}", user.getName(), user.getEmail());
                return true;
            } catch (Exception e) {
                log.error("Authorization failure: {}", e.getMessage(), e);
                throw new AuthorizationException(e.getMessage());
            }
        }

        return false;
    }
}

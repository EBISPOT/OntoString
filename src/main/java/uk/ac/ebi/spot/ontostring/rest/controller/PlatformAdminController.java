package uk.ac.ebi.spot.ontostring.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontostring.service.ExternalServiceConfigService;
import uk.ac.ebi.spot.ontostring.service.JWTService;
import uk.ac.ebi.spot.ontostring.service.ProjectAdminService;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.util.HeadersUtil;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.exception.AuthorizationException;
import uk.ac.ebi.spot.ontostring.rest.assembler.ExternalServiceConfigDtoAssembler;
import uk.ac.ebi.spot.ontostring.rest.dto.config.ExternalServiceConfigDto;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PLATFORM_ADMIN)
public class PlatformAdminController {

    private static final Logger log = LoggerFactory.getLogger(PlatformAdminController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ExternalServiceConfigService externalServiceConfigService;

    @Autowired(required = false)
    private ProjectAdminService projectAdminService;

    /**
     * PUT /v1/platform-admin
     */
    @PutMapping(consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public ExternalServiceConfigDto updateConfig(@RequestBody @Valid ExternalServiceConfigDto externalServiceConfigDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to update config for service: {}", user.getEmail(), externalServiceConfigDto.getServiceName());
        if (!user.isSuperUser()) {
            log.error("Invalid access control for user: {}", user.getEmail());
            throw new AuthorizationException("Invalid access control for user: " + user.getEmail());
        }

        ExternalServiceConfig updated = externalServiceConfigService.updateConfig(ExternalServiceConfigDtoAssembler.disassemble(externalServiceConfigDto));
        return ExternalServiceConfigDtoAssembler.assemble(updated);
    }

    /**
     * GET /v1/platform-admin
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ExternalServiceConfigDto> getConfigs(HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve platform config.", user.getEmail());
        if (!user.isSuperUser()) {
            log.error("Invalid access control for user: {}", user.getEmail());
            throw new AuthorizationException("Invalid access control for user: " + user.getEmail());
        }

        List<ExternalServiceConfig> configs = externalServiceConfigService.retrieveConfigs();
        return configs.stream().map(ExternalServiceConfigDtoAssembler::assemble).collect(Collectors.toList());
    }

    /**
     * GET /v1/platform-admin/run-matchmaking
     */
    @GetMapping(value = CurationConstants.API_RUN_MATCHMAKING)
    @ResponseStatus(HttpStatus.OK)
    public void runMatchmaking(HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve platform config.", user.getEmail());
        if (!user.isSuperUser()) {
            log.error("Invalid access control for user: {}", user.getEmail());
            throw new AuthorizationException("Invalid access control for user: " + user.getEmail());
        }

        if (projectAdminService != null) {
            projectAdminService.runMatchmaking();
        } else {
            log.error("Unable to rerun matchmaking. Update schedule for Zooma is disabled.");
        }
    }
}

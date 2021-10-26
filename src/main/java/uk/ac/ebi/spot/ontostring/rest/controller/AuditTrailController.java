package uk.ac.ebi.spot.ontostring.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontostring.domain.AuditEntry;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.service.AuditEntryService;
import uk.ac.ebi.spot.ontostring.service.EntityService;
import uk.ac.ebi.spot.ontostring.service.JWTService;
import uk.ac.ebi.spot.ontostring.service.ProjectService;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.util.HeadersUtil;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.ProjectRole;
import uk.ac.ebi.spot.ontostring.rest.assembler.AuditEntryDtoAssembler;
import uk.ac.ebi.spot.ontostring.rest.dto.audit.AuditEntryDto;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class AuditTrailController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private AuditEntryService auditEntryService;

    /**
     * GET /v1/projects/{projectId}/entities/{entityId}/audit-trail
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_ENTITIES + "/{entityId}" + CurationConstants.API_AUDIT_TRAIL,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<AuditEntryDto> getEntities(@PathVariable String projectId,
                                           @PathVariable String entityId,
                                           HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve audit trail for entity: {} | {}", user.getEmail(), projectId, entityId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Entity entity = entityService.retrieveEntity(entityId);
        List<AuditEntry> auditTrail = auditEntryService.retrieveAuditEntries(entity.getId());
        return auditTrail.stream().map(AuditEntryDtoAssembler::assemble).collect(Collectors.toList());
    }
}

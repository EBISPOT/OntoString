package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class ProjectsUtilController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsUtilController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OLSService olsService;

    /**
     * GET /v1/projects/{projectId}/searchOLS?query=<query>
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_SEARCH_OLS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<OLSQueryDocDto> getProject(@PathVariable String projectId,
                                           @RequestParam(value = CurationConstants.PARAM_QUERY) String query,
                                           HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to query OLS [{}]: {}", user.getEmail(), projectId, query);
        return olsService.query(query);
    }
}

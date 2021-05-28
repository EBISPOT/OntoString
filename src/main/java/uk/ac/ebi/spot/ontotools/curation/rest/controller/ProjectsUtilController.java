package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.OLSQueryResultsType;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryResultDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class ProjectsUtilController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsUtilController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OLSService olsService;

    @Autowired
    private ProjectService projectService;

    /**
     * GET /v1/projects/{projectId}/searchOLS?query=<QUERY>&context=<CONTEXT>
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_SEARCH_OLS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OLSQueryResultDto queryTerms(@PathVariable String projectId,
                                        @RequestParam(value = CurationConstants.PARAM_QUERY) String query,
                                        @RequestParam(value = CurationConstants.PARAM_CONTEXT) String context,
                                        HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to query OLS [{}]: {} | {}", user.getEmail(), projectId, query, context);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Project project = projectService.retrieveProject(projectId, user);
        Map<String, List<OLSQueryDocDto>> resultsMap = new LinkedHashMap<>();
        resultsMap.put(OLSQueryResultsType.ALL.name(), olsService.query(query, project, context, false, false));
        resultsMap.put(OLSQueryResultsType.GRAPH_RESTRICTION.name(), olsService.query(query, project, context, false, true));
        resultsMap.put(OLSQueryResultsType.PREFERRED_ONTOLOGIES.name(), olsService.query(query, project, context, true, false));
        return new OLSQueryResultDto(resultsMap);
    }
}

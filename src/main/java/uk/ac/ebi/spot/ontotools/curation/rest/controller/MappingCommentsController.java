package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.CommentDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.CommentDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotEmpty;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class MappingCommentsController {

    private static final Logger log = LoggerFactory.getLogger(MappingCommentsController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MappingService mappingService;

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/comments
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS + "/{mappingId}" + CurationConstants.API_COMMENTS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public CommentDto createComment(@PathVariable String projectId, @PathVariable String mappingId,
                                    @RequestBody @NotEmpty String body, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to create comment on mapping: {} | {}", user.getEmail(), projectId, mappingId);
        projectService.verifyAccess(projectId, user, Arrays.asList(ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER));

        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        Mapping mapping = mappingService.addCommentToMapping(mappingId, body, provenance);
        return CommentDtoAssembler.assemble(mapping.getComments().get(mapping.getComments().size() - 1));
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/comments
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS + "/{mappingId}" + CurationConstants.API_COMMENTS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<CommentDto> retrieveReviews(@PathVariable String projectId, @PathVariable String mappingId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve comments for mapping: {} | {}", user.getEmail(), projectId, mappingId);
        projectService.verifyAccess(projectId, user, Arrays.asList(ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER));

        Mapping mapping = mappingService.retrieveMappingById(mappingId);
        return mapping.getReviews() != null ? mapping.getComments().stream().map(CommentDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>();
    }
}

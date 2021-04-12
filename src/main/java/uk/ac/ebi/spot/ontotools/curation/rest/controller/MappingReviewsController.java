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
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.ReviewDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ReviewDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class MappingReviewsController {

    private static final Logger log = LoggerFactory.getLogger(MappingReviewsController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MappingService mappingService;

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS + "/{mappingId}" + CurationConstants.API_REVIEWS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public ReviewDto createReview(@PathVariable String projectId, @PathVariable String mappingId,
                                  @RequestBody String comment, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to create review on mapping: {} | {}", user.getEmail(), projectId, mappingId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));

        Project project = projectService.retrieveProject(projectId, user);
        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        Mapping mapping = mappingService.addReviewToMapping(mappingId, comment,
                project.getNumberOfReviewsRequired() == null ? 0 : project.getNumberOfReviewsRequired(), provenance);
        return ReviewDtoAssembler.assemble(mapping.getReviews().get(mapping.getReviews().size() - 1));
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS + "/{mappingId}" + CurationConstants.API_REVIEWS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<ReviewDto> retrieveReviews(@PathVariable String projectId, @PathVariable String mappingId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve reviews for mapping: {} | {}", user.getEmail(), projectId, mappingId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));

        Mapping mapping = mappingService.retrieveMappingById(mappingId);
        return mapping.getReviews() != null ? mapping.getReviews().stream().map(ReviewDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>();
    }
}

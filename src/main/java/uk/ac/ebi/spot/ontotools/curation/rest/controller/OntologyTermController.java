package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.ExtendedOntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.ExtendedOntologyTermDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.OntologyTermDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.RestResponsePage;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.*;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermUtilService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class OntologyTermController {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private OntologyTermService ontologyTermService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private OntologyTermUtilService ontologyTermUtilService;

    /**
     * POST /v1/projects/{projectId}/ontology-terms
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_ONTOLOGY_TERMS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public OntologyTermDto createOntologyTerm(@PathVariable String projectId, @RequestBody @Valid OntologyTermCreationDto ontologyTermCreationDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to create ontology term: {} | {}", user.getEmail(), projectId, ontologyTermCreationDto.getOntologyTerm().getCurie());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));
        OntologyTerm created = ontologyTermService.createTerm(OntologyTermDtoAssembler.disassemble(ontologyTermCreationDto.getOntologyTerm(), projectId,
                ontologyTermCreationDto.getContext()));
        return OntologyTermDtoAssembler.assemble(created, projectId, ontologyTermCreationDto.getContext());
    }

    /**
     * GET /v1/projects/{projectId}/ontology-terms?status=<STATUS>&context=<CONTEXT>
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_ONTOLOGY_TERMS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public RestResponsePage<ExtendedOntologyTermDto> getOntologyTerms(@PathVariable String projectId,
                                                                      @RequestParam(value = CurationConstants.PARAM_STATUS) String status,
                                                                      @RequestParam(value = CurationConstants.PARAM_CONTEXT) String context,
                                                                      @PageableDefault(size = 20, page = 0) Pageable pageable,
                                                                      HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to get ontology terms: {} | {} | {}", user.getEmail(), projectId, status, context);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Page<OntologyTerm> ontologyTermsPage = ontologyTermService.retrieveTermsByStatus(projectId, context, status, pageable);
        Map<String, Map<String, String>> entityData = ontologyTermUtilService.retrieveEntityData(ontologyTermsPage.getContent(), projectId, context);
        List<ExtendedOntologyTermDto> extendedOntologyTermDtos = new ArrayList<>();
        for (OntologyTerm ontologyTerm : ontologyTermsPage.getContent()) {
            Map<String, String> entityMap = entityData.get(ontologyTerm.getId());
            extendedOntologyTermDtos.add(ExtendedOntologyTermDtoAssembler.assemble(new ExtendedOntologyTerm(entityMap, ontologyTerm),
                    projectId, context));
        }
        return new RestResponsePage<>(extendedOntologyTermDtos, pageable, ontologyTermsPage.getTotalElements());
    }

    /**
     * GET /v1/projects/{projectId}/ontology-terms-stats?context=<CONTEXT>
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_ONTOLOGY_TERMS_STATS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public OntologyTermStatsDto getOntologyTermStats(@PathVariable String projectId,
                                                     @RequestParam(value = CurationConstants.PARAM_CONTEXT) String context,
                                                     HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to get ontology term stats: {} | {}", user.getEmail(), projectId, context);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Map<String, Integer> stats = ontologyTermService.retrieveTermStats(projectId, context);
        return new OntologyTermStatsDto(stats);
    }

    /**
     * POST /v1/projects/{projectId}/ontology-terms/action
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_ONTOLOGY_TERMS + CurationConstants.API_ACTION,
            consumes = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void actionOntologyTerms(@PathVariable String projectId, @RequestBody @Valid ActionOntologyTermsDto actionOntologyTermsDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to action ontology terms: {} | {} | {}", user.getEmail(), projectId,
                actionOntologyTermsDto.getStatus(), actionOntologyTermsDto.getContext());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));
        ontologyTermUtilService.actionTerms(projectId, actionOntologyTermsDto.getContext(),
                actionOntologyTermsDto.getStatus(), actionOntologyTermsDto.getComment(), user);
    }

    /**
     * POST /v1/projects/{projectId}/ontology-terms/export
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_ONTOLOGY_TERMS + CurationConstants.API_EXPORT,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public HttpEntity<byte[]> exportOntologyTerms(@PathVariable String projectId, @RequestBody @Valid ExportOntologyTermsDto exportOntologyTermsDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to export ontology terms: {} | {} | {}", user.getEmail(), projectId,
                exportOntologyTermsDto.getStatus(), exportOntologyTermsDto.getContext());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));

        String csvContent = ontologyTermUtilService.exportOntologyTerms(projectId, exportOntologyTermsDto.getContext(),
                exportOntologyTermsDto.getStatus());
        byte[] payload = csvContent.getBytes(StandardCharsets.UTF_8);
        HttpHeaders responseHeaders = new HttpHeaders();
        responseHeaders.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=terms_" + projectId + "_" +
                exportOntologyTermsDto.getContext() + ".csv");
        responseHeaders.add(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE);
        responseHeaders.add(HttpHeaders.CONTENT_LENGTH, Integer.toString(payload.length));
        return new HttpEntity<>(payload, responseHeaders);
    }
}

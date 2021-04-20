package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.OntologyTermDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.RestResponsePage;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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
    public RestResponsePage<OntologyTermDto> getOntologyTerms(@PathVariable String projectId,
                                                              @RequestParam(value = CurationConstants.PARAM_STATUS) String status,
                                                              @RequestParam(value = CurationConstants.PARAM_CONTEXT) String context,
                                                              @PageableDefault(size = 20, page = 0) Pageable pageable,
                                                              HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to get ontology terms: {} | {} | {}", user.getEmail(), projectId, status, context);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Page<OntologyTerm> ontologyTerms = ontologyTermService.retrieveTermsByStatus(projectId, context, status, pageable);
        List<OntologyTermDto> ontologyTermDtos = new ArrayList<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontologyTermDtos.add(OntologyTermDtoAssembler.assemble(ontologyTerm, projectId, context));
        }
        return new RestResponsePage<>(ontologyTermDtos, pageable, ontologyTerms.getTotalElements());
    }
}

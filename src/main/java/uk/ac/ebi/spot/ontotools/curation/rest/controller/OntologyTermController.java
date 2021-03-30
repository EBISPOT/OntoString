package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.OntologyTermDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.JWTService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class OntologyTermController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);

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

}

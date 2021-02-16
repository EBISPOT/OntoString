package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.apache.commons.io.IOUtils;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.SourceDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class SourcesController {

    private static final Logger log = LoggerFactory.getLogger(SourcesController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private MatchmakerService matchmakerService;

    @Autowired
    private DataImportService dataImportService;

    /**
     * POST /v1/projects/{projectId}/sources
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_SOURCES,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public SourceDto createSource(@RequestBody @Valid SourceCreationDto sourceCreationDto, @PathVariable String projectId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{} | {}] Request to create source: {}", user.getEmail(), projectId, sourceCreationDto.getName());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));
        Source created = sourceService.createSource(SourceDtoAssembler.disassemble(sourceCreationDto, new Provenance(user.getName(), user.getEmail(), DateTime.now())), projectId);
        return SourceDtoAssembler.assemble(created);
    }

    /**
     * GET /v1/projects/{projectId}/sources
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_SOURCES,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<SourceDto> getSources(@PathVariable String projectId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve sources: {}", user.getEmail(), projectId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        List<Source> sources = sourceService.getSources(projectId);
        log.info("Found {} sources in project: {}", sources.size(), projectId);
        return sources.stream().map(SourceDtoAssembler::assemble).collect(Collectors.toList());
    }

    /**
     * GET /v1/projects/{projectId}/sources/{sourceId}
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_SOURCES + "/{sourceId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public SourceDto getSource(@PathVariable String projectId, @PathVariable String sourceId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve source: {} | {}", user.getEmail(), projectId, sourceId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Source source = sourceService.getSource(sourceId, projectId);
        return SourceDtoAssembler.assemble(source);
    }

    /**
     * POST /v1/projects/{projectId}/sources/{sourceId}
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_SOURCES + "/{sourceId}",
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void addDataToSource(@RequestBody List<String> entities, @PathVariable String projectId, @PathVariable String sourceId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to add data to source: {} | {} | {}", user.getEmail(), projectId, sourceId, entities);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));
        Source source = sourceService.getSource(sourceId, projectId);
        for (String entity : entities) {
            entityService.createEntity(new Entity(null, entity, null, null,
                    source.getId(), new Provenance(user.getName(), user.getEmail(), DateTime.now()), EntityStatus.UNMAPPED));
        }
        matchmakerService.runMatchmaking(sourceId, projectService.retrieveProject(projectId, user));
    }

    /**
     * POST /v1/projects/{projectId}/sources/{sourceId}/upload
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_SOURCES + "/{sourceId}" + CurationConstants.API_UPLOAD,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public void importDataFromFile(@RequestParam MultipartFile file, @PathVariable String projectId, @PathVariable String sourceId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to import data from file [{} - {}] to source: {} | {}", user.getEmail(), file.getOriginalFilename(), file.getSize(), projectId, sourceId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));

        try {
            String fileData = IOUtils.toString(file.getInputStream(), "UTF-8");
            dataImportService.importData(fileData, projectId, sourceId, user);
        } catch (IOException e) {
            log.error("Unable to deserialize import data file: {}", e.getMessage(), e);
        }

    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.EntityDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.SourceDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingCreationDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;
import uk.ac.ebi.spot.ontotools.curation.util.HeadersUtil;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS)
public class MappingsController {

    private static final Logger log = LoggerFactory.getLogger(ProjectsController.class);

    @Autowired
    private JWTService jwtService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private EntityService entityService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private SourceService sourceService;

    @Autowired
    private MappingSuggestionsService mappingSuggestionsService;

    @Autowired
    private OntologyTermService ontologyTermService;

    /**
     * GET /v1/projects/{projectId}/mappings?entityId=<entityId>
     */
    @GetMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EntityDto getMappings(@PathVariable String projectId, @RequestParam(value = CurationConstants.PARAM_ENTITY_ID) String entityId, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to retrieve mappings: {} | {}", user.getEmail(), projectId, entityId);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR, ProjectRole.CONSUMER}));
        Entity entity = entityService.retrieveEntity(entityId);
        return packAndSend(entity, projectId);
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     * <p>
     * Assumptions:
     * - Creating a mapping = transforming an existing mapping suggestion into a new mapping.
     * - No existing mappings are deleted
     * - Originating mapping suggestion is removed
     */
    @PostMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS,
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.CREATED)
    public EntityDto createMapping(@PathVariable String projectId, @RequestBody @Valid MappingCreationDto mappingCreationDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to create mapping: {} | {} | {}", user.getEmail(), projectId, mappingCreationDto.getEntityId(), mappingCreationDto.getOntologyTerm());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));

        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        Entity entity = entityService.retrieveEntity(mappingCreationDto.getEntityId());
        OntologyTerm ontologyTerm = ontologyTermService.retrieveTermByCurie(mappingCreationDto.getOntologyTerm().getCurie());

        /**
         * Check if a mapping to this term already exists
         */
        List<Mapping> existingMappings = mappingService.retrieveMappingsForEntity(entity.getId());
        boolean exists = false;
        for (Mapping mapping : existingMappings) {
            if (mapping.getOntologyTermIds().contains(ontologyTerm.getId())) {
                exists = true;
                break;
            }
        }
        if (exists) {
            log.warn("[{}] Mapping to term [{}] already exists.", entity.getName(), ontologyTerm.getCurie());
            return packAndSend(entity, projectId);
        }

        /**
         * Create new mapping.
         */
        mappingService.createMapping(entity, ontologyTerm, provenance);

        /**
         * Updating mapping status to MANUAL.
         */
        entity = entityService.updateMappingStatus(entity, EntityStatus.MANUALLY_MAPPED);

        /**
         * Deleting mapping suggestions associated with the current ontology term.
         */
        mappingSuggestionsService.deleteMappingSuggestions(entity.getId(), ontologyTerm.getId());

        return packAndSend(entity, projectId);
    }

    /**
     * PUT /v1/projects/{projectId}/mappings/{mappingId}
     * <p>
     * Assumptions:
     * - Updating a mapping = adding an additional ontology term from a mapping suggestion to an existing mapping
     * - No existing mappings are deleted
     * - Originating mapping suggestion is removed
     */
    @PutMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS + "/{mappingId}",
            consumes = MediaType.APPLICATION_JSON_VALUE,
            produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public EntityDto updateMapping(@PathVariable String projectId, @PathVariable String mappingId, @RequestBody @Valid MappingCreationDto mappingCreationDto, HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to update mapping [{} - {}]: {} | {}", user.getEmail(), projectId, mappingId, mappingCreationDto.getEntityId(), mappingCreationDto.getOntologyTerm());
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));

        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        Entity entity = entityService.retrieveEntity(mappingCreationDto.getEntityId());
        OntologyTerm ontologyTerm = ontologyTermService.retrieveTermByCurie(mappingCreationDto.getOntologyTerm().getCurie());

        /**
         * Check if a different mapping to this term already exists
         */
        List<Mapping> existingMappings = mappingService.retrieveMappingsForEntity(entity.getId());
        boolean exists = false;
        for (Mapping mapping : existingMappings) {
            if (mapping.getOntologyTermIds().contains(ontologyTerm.getId())) {
                exists = true;
                break;
            }
        }
        if (exists) {
            log.warn("[{}] Mapping to term [{}] already exists.", entity.getName(), ontologyTerm.getCurie());
            return packAndSend(entity, projectId);
        }

        /**
         * Update mapping.
         */
        mappingService.updateMapping(mappingId, ontologyTerm, provenance);

        /**
         * Updating mapping status to MANUAL.
         */
        entity = entityService.updateMappingStatus(entity, EntityStatus.MANUALLY_MAPPED);

        /**
         * Deleting mapping suggestions associated with the current ontology term.
         */
        mappingSuggestionsService.deleteMappingSuggestions(entity.getId(), ontologyTerm.getId());

        return packAndSend(entity, projectId);
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}?curie=<CURIE>
     * <p>
     * Assumptions:
     * - Delete entire mapping when curie is missing
     * - Delete only mapping to curie when this is present
     * - Convert all removed curies to mapping suggestions
     */
    @DeleteMapping(value = "/{projectId}" + CurationConstants.API_MAPPINGS + "/{mappingId}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteMapping(@PathVariable String projectId, @PathVariable String mappingId,
                              @RequestParam(value = CurationConstants.PARAM_CURIE, required = false) String curie,
                              HttpServletRequest request) {
        User user = jwtService.extractUser(HeadersUtil.extractJWT(request));
        log.info("[{}] Request to delete mapping [{} - {}]: {}", user.getEmail(), projectId, mappingId, curie);
        projectService.verifyAccess(projectId, user, Arrays.asList(new ProjectRole[]{ProjectRole.ADMIN, ProjectRole.CONTRIBUTOR}));

        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        Mapping mapping = mappingService.retrieveMappingById(mappingId);
        Entity entity = entityService.retrieveEntity(mapping.getEntityId());

        /**
         * Delete mapping.
         */
        OntologyTerm ontoTerm = curie != null ? ontologyTermService.retrieveTermByCurie(curie) : null;
        List<String> ontoTermIds = mappingService.deleteMapping(mappingId, ontoTerm != null ? ontoTerm.getId() : null, provenance);

        /**
         * Re-create mapping suggestions.
         */
        for (String ontoTermId : ontoTermIds) {
            OntologyTerm ontologyTerm = ontologyTermService.retrieveTermById(ontoTermId);
            mappingSuggestionsService.createMappingSuggestion(entity, ontologyTerm, provenance);
        }

    }

    private EntityDto packAndSend(Entity entity, String projectId) {
        List<Source> sources = sourceService.getSources(projectId);
        Map<String, SourceDto> sourceMap = new HashMap<>();
        for (Source source : sources) {
            sourceMap.put(source.getId(), SourceDtoAssembler.assemble(source));
        }
        List<Mapping> mappings = mappingService.retrieveMappingsForEntity(entity.getId());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionsService.retrieveMappingSuggestionsForEntity(entity.getId());
        return EntityDtoAssembler.assemble(entity, sourceMap.get(entity.getSourceId()), mappings, mappingSuggestions);

    }

}

package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.*;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.OntologyTermDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.AuditEntryDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.MetadataEntryDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class MappingsControllerTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    private Project project;

    private SourceDto sourceDto;

    @Override
    public void setup() throws Exception {
        super.setup();
        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0);
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
        sourceDto = super.createSource(project.getId());

        super.createEntityTestData(sourceDto.getId(), project.getId(), user1);
    }

    /**
     * GET /v1/projects/{projectId}/mappings?entityId=<entityId>
     */
    @Test
    public void shouldGetMappings() throws Exception {
        MappingDto actual = super.retrieveMapping(project.getId());
        assertEquals("Orphanet:15", actual.getOntologyTerms().get(0).getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.getStatus());
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     */
    @Test
    public void shouldCreateMapping() throws Exception {
        OntologyTerm ontologyTerm = ontologyTermRepository.findByCurie("MONDO:0007037").get();
        OntologyTermDto ontologyTermDto = OntologyTermDtoAssembler.assemble(ontologyTerm, project.getId(), entity.getContext());
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entity.getId(), Arrays.asList(new OntologyTermDto[]{ontologyTermDto}));
        mappingRepository.deleteAll();

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MappingDto actual = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(1, actual.getOntologyTerms().size());
        assertEquals("Achondroplasia", actual.getOntologyTerms().get(0).getLabel());
        assertEquals("MONDO:0007037", actual.getOntologyTerms().get(0).getCurie());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(EntityStatus.MANUALLY_MAPPED.name(), actualEntity.getMappingStatus());

        assertNotNull(actualEntity.getMapping());
        assertEquals(1, actualEntity.getMapping().getOntologyTerms().size());
        assertEquals("Achondroplasia", actualEntity.getMapping().getOntologyTerms().get(0).getLabel());
        assertEquals("MONDO:0007037", actualEntity.getMapping().getOntologyTerms().get(0).getCurie());

        assertEquals(2, actualEntity.getMappingSuggestions().size());
        List<String> curies = new ArrayList<>();
        for (MappingSuggestionDto mappingSuggestionDto : actualEntity.getMappingSuggestions()) {
            curies.add(mappingSuggestionDto.getOntologyTerm().getCurie());
        }
        assertTrue(curies.contains("Orphanet:15"));
        assertTrue(curies.contains("MONDO:0007037"));

        assertEquals(sourceDto.getId(), actualEntity.getSource().getId());

        assertEquals(1, actualEntity.getAuditTrail().size());
        AuditEntryDto auditEntryDto = actualEntity.getAuditTrail().get(0);
        assertTrue(auditEntryDto.getAction().equalsIgnoreCase(AuditEntryConstants.ADDED_MAPPING.name()));
        assertEquals(1, auditEntryDto.getMetadata().size());
        assertEquals("http://purl.obolibrary.org/obo/MONDO_0007037", auditEntryDto.getMetadata().get(0).getKey());
    }

    /**
     * PUT /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldUpdateMapping() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        List<OntologyTermDto> ontologyTermDtos = new ArrayList<>();
        ontologyTermDtos.add(OntologyTermDtoAssembler.assemble(ontologyTermRepository.findByCurie("MONDO:0007037").get(), project.getId(), entity.getContext()));
        ontologyTermDtos.add(OntologyTermDtoAssembler.assemble(ontologyTermRepository.findByCurie("Orphanet:15").get(), project.getId(), entity.getContext()));

        MappingDto updated = new MappingDto(mappingDto.getId(),
                mappingDto.getEntityId(),
                ontologyTermDtos,
                mappingDto.isReviewed(),
                mappingDto.getStatus(),
                mappingDto.getReviews(),
                mappingDto.getComments(),
                mappingDto.getCreated());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + mappingDto.getId();
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updated))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MappingDto actual = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, actual.getOntologyTerms().size());
        List<String> curies = new ArrayList<>();
        for (OntologyTermDto ontologyTermDto : actual.getOntologyTerms()) {
            curies.add(ontologyTermDto.getCurie());
        }

        assertTrue(curies.contains("Orphanet:15"));
        assertTrue(curies.contains("MONDO:0007037"));

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(EntityStatus.MANUALLY_MAPPED.name(), actualEntity.getMappingStatus());

        assertNotNull(actualEntity.getMapping());
        assertEquals(2, actualEntity.getMapping().getOntologyTerms().size());

        assertEquals(2, actualEntity.getMappingSuggestions().size());
        curies = new ArrayList<>();
        for (MappingSuggestionDto mappingSuggestionDto : actualEntity.getMappingSuggestions()) {
            curies.add(mappingSuggestionDto.getOntologyTerm().getCurie());
        }
        assertTrue(curies.contains("Orphanet:15"));
        assertTrue(curies.contains("MONDO:0007037"));

        assertEquals(1, actualEntity.getAuditTrail().size());
        AuditEntryDto auditEntryDto = actualEntity.getAuditTrail().get(0);
        assertTrue(auditEntryDto.getAction().equalsIgnoreCase(AuditEntryConstants.UPDATED_MAPPING.name()));
        assertEquals(2, auditEntryDto.getMetadata().size());

        curies = new ArrayList<>();
        for (MetadataEntryDto metadataEntryDto : auditEntryDto.getMetadata()) {
            curies.add(metadataEntryDto.getKey());
        }

        assertTrue(curies.contains("http://purl.obolibrary.org/obo/MONDO_0007037"));
        assertTrue(curies.contains("http://www.orpha.net/ORDO/Orphanet_15"));
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldDeleteMapping() throws Exception {
        mappingSuggestionRepository.deleteAll();
        MappingDto actual = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + actual.getId();
        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(EntityStatus.SUGGESTIONS_PROVIDED.name(), actualEntity.getMappingStatus());

        assertNull(actualEntity.getMapping());

        assertEquals(1, actualEntity.getMappingSuggestions().size());
        assertEquals("Orphanet:15", actualEntity.getMappingSuggestions().get(0).getOntologyTerm().getCurie());

        assertEquals(2, actualEntity.getAuditTrail().size());
        for (AuditEntryDto auditEntryDto : actualEntity.getAuditTrail()) {
            assertTrue(auditEntryDto.getAction().equalsIgnoreCase(AuditEntryConstants.REMOVED_MAPPING.name()) ||
                    auditEntryDto.getAction().equalsIgnoreCase(AuditEntryConstants.ADDED_SUGGESTION.name()));
            if (auditEntryDto.getAction().equalsIgnoreCase(AuditEntryConstants.ADDED_SUGGESTION.name())) {
                assertEquals(1, auditEntryDto.getMetadata().size());
                assertEquals("http://www.orpha.net/ORDO/Orphanet_15", auditEntryDto.getMetadata().get(0).getKey());
            }
            if (auditEntryDto.getAction().equalsIgnoreCase(AuditEntryConstants.REMOVED_MAPPING.name())) {
                assertEquals(1, auditEntryDto.getMetadata().size());
                assertEquals("http://www.orpha.net/ORDO/Orphanet_15", auditEntryDto.getMetadata().get(0).getKey());
            }
        }
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     */
    @Test
    public void shouldNotCreateMapping() throws Exception {
        OntologyTermDto ontologyTermDto = new OntologyTermDto("MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037", "Achondroplasia", TermStatus.NEEDS_IMPORT.name(), null, null);
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entity.getId(), Arrays.asList(new OntologyTermDto[]{ontologyTermDto}));
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     */
    @Test
    public void shouldNotCreateMappingAsConsumer() throws Exception {
        OntologyTermDto ontologyTermDto = new OntologyTermDto("MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037", "Achondroplasia", TermStatus.NEEDS_IMPORT.name(), null, null);
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entity.getId(), Arrays.asList(new OntologyTermDto[]{ontologyTermDto}));
        userService.addUserToProject(super.user2, project.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldNotDeleteMapping() throws Exception {
        MappingDto actual = super.retrieveMapping(project.getId());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + actual.getId();
        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldNotDeleteMappingAsConsumer() throws Exception {
        MappingDto actual = super.retrieveMapping(project.getId());
        userService.addUserToProject(super.user2, project.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + actual.getId();
        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

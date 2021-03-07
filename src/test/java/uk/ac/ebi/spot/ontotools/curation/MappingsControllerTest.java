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
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
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
        ProjectDto projectDto = super.createProject("New Project", "token1", datasources, ontologies, "efo", 0);
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
        List<MappingDto> actual = super.retrieveMapping(project.getId());
        assertEquals(1, actual.size());
        assertEquals("Orphanet:15", actual.get(0).getOntologyTerms().get(0).getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.get(0).getStatus());
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     */
    @Test
    public void shouldCreateMapping() throws Exception {
        OntologyTerm ontologyTerm = ontologyTermRepository.findByCurie("MONDO:0007037").get();
        OntologyTermDto ontologyTermDto = OntologyTermDtoAssembler.assemble(ontologyTerm);
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entity.getId(), ontologyTermDto);
        mappingRepository.deleteAll();

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MappingDto actual = mapper.readValue(response, new TypeReference<MappingDto>() {
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

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals(EntityStatus.MANUALLY_MAPPED.name(), actualEntity.getMappingStatus());

        assertNotNull(actualEntity.getMapping());
        assertEquals(1, actualEntity.getMapping().getOntologyTerms().size());
        assertEquals("Achondroplasia", actualEntity.getMapping().getOntologyTerms().get(0).getLabel());
        assertEquals("MONDO:0007037", actualEntity.getMapping().getOntologyTerms().get(0).getCurie());

        assertEquals(1, actualEntity.getMappingSuggestions().size());
        assertEquals("Orphanet:15", actualEntity.getMappingSuggestions().get(0).getOntologyTerm().getCurie());

        assertEquals(sourceDto.getId(), actualEntity.getSource().getId());
    }

    /**
     * PUT /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldUpdateMapping() throws Exception {
        List<MappingDto> mappingDtos = super.retrieveMapping(project.getId());
        List<OntologyTermDto> ontologyTermDtos = new ArrayList<>();
        ontologyTermDtos.add(OntologyTermDtoAssembler.assemble(ontologyTermRepository.findByCurie("MONDO:0007037").get()));
        ontologyTermDtos.add(OntologyTermDtoAssembler.assemble(ontologyTermRepository.findByCurie("Orphanet:15").get()));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + mappingDtos.get(0).getId();
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(ontologyTermDtos))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MappingDto actual = mapper.readValue(response, new TypeReference<MappingDto>() {
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

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals(EntityStatus.MANUALLY_MAPPED.name(), actualEntity.getMappingStatus());

        assertNotNull(actualEntity.getMapping());
        assertEquals(2, actualEntity.getMapping().getOntologyTerms().size());

        assertEquals(0, actualEntity.getMappingSuggestions().size());
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldDeleteMapping() throws Exception {
        List<MappingDto> actual = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + actual.get(0).getId();
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

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals(EntityStatus.SUGGESTIONS_PROVIDED.name(), actualEntity.getMappingStatus());

        assertNull(actualEntity.getMapping());

        assertEquals(2, actualEntity.getMappingSuggestions().size());
        List<String> curies = new ArrayList<>();
        for (MappingSuggestionDto mappingSuggestionDto : actualEntity.getMappingSuggestions()) {
            curies.add(mappingSuggestionDto.getOntologyTerm().getCurie());
        }

        assertTrue(curies.contains("Orphanet:15"));
        assertTrue(curies.contains("MONDO:0007037"));
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     */
    @Test
    public void shouldNotCreateMapping() throws Exception {
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entity.getId(),
                new OntologyTermDto("MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037", "Achondroplasia", TermStatus.NEEDS_IMPORT.name(), null, null));
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
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entity.getId(),
                new OntologyTermDto("MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037", "Achondroplasia", TermStatus.NEEDS_IMPORT.name(), null, null));
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
        List<MappingDto> actual = super.retrieveMapping(project.getId());
        MappingDto mappingDto = actual.get(0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + mappingDto.getId();
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
        List<MappingDto> actual = super.retrieveMapping(project.getId());
        MappingDto mappingDto = actual.get(0);
        userService.addUserToProject(super.user2, project.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + mappingDto.getId();
        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

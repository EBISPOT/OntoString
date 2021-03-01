package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
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
        EntityDto actual = super.retrieveEntity(project.getId());
        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.AUTO_MAPPED.name(), actual.getMappingStatus());

        assertEquals(1, actual.getMappings().size());
        assertEquals("Orphanet:15", actual.getMappings().get(0).getOntologyTerms().get(0).getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.getMappings().get(0).getStatus());

        assertEquals(2, actual.getMappingSuggestions().size());
        int foundCuries = 0;
        for (MappingSuggestionDto mappingSuggestion : actual.getMappingSuggestions()) {
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        assertEquals(sourceDto.getId(), actual.getSource().getId());
    }

    /**
     * POST /v1/projects/{projectId}/mappings
     */
    @Test
    public void shouldCreateMapping() throws Exception {
        EntityDto entityDto = super.retrieveEntity(project.getId());
        OntologyTermDto ontologyTermDto = null;
        for (MappingSuggestionDto mappingSuggestionDto : entityDto.getMappingSuggestions()) {
            if (mappingSuggestionDto.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                ontologyTermDto = mappingSuggestionDto.getOntologyTerm();
                break;
            }
        }
        assertNotNull(ontologyTermDto);
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entityDto.getId(), ontologyTermDto);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actual = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.MANUALLY_MAPPED.name(), actual.getMappingStatus());

        assertEquals(2, actual.getMappings().size());
        int foundCuries = 0;
        for (MappingDto mappingDto : actual.getMappings()) {
            if (mappingDto.getOntologyTerms().get(0).getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (mappingDto.getOntologyTerms().get(0).getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        for (MappingDto mappingDto : actual.getMappings()) {
            assertEquals(MappingStatus.AWAITING_REVIEW.name(), mappingDto.getStatus());
        }

        assertEquals(1, actual.getMappingSuggestions().size());
        assertEquals("Orphanet:15", actual.getMappingSuggestions().get(0).getOntologyTerm().getCurie());

        assertEquals(sourceDto.getId(), actual.getSource().getId());
    }

    /**
     * PUT /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldUpdateMapping() throws Exception {
        EntityDto entityDto = super.retrieveEntity(project.getId());
        OntologyTermDto ontologyTermDto = null;
        for (MappingSuggestionDto mappingSuggestionDto : entityDto.getMappingSuggestions()) {
            if (mappingSuggestionDto.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                ontologyTermDto = mappingSuggestionDto.getOntologyTerm();
                break;
            }
        }
        assertNotNull(ontologyTermDto);
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entityDto.getId(), ontologyTermDto);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + orphaTermMapping.getId();
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actual = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.MANUALLY_MAPPED.name(), actual.getMappingStatus());

        assertEquals(1, actual.getMappings().size());
        int foundCuries = 0;
        for (OntologyTermDto ontologyTerm : actual.getMappings().get(0).getOntologyTerms()) {
            if (ontologyTerm.getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (ontologyTerm.getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        assertEquals(1, actual.getMappingSuggestions().size());
        assertEquals("Orphanet:15", actual.getMappingSuggestions().get(0).getOntologyTerm().getCurie());
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}?curie=<CURIE>
     */
    @Test
    public void shouldDeleteMappingByCurie() throws Exception {
        EntityDto entityDto = super.retrieveEntity(project.getId());
        OntologyTermDto ontologyTermDto = null;
        for (MappingSuggestionDto mappingSuggestionDto : entityDto.getMappingSuggestions()) {
            if (mappingSuggestionDto.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                ontologyTermDto = mappingSuggestionDto.getOntologyTerm();
                break;
            }
        }
        assertNotNull(ontologyTermDto);
        MappingCreationDto mappingCreationDto = new MappingCreationDto(entityDto.getId(), ontologyTermDto);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + orphaTermMapping.getId();
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(mappingCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actual = mapper.readValue(response, new TypeReference<EntityDto>() {
        });

        assertEquals(1, actual.getMappings().size());
        int foundCuries = 0;
        for (OntologyTermDto ontologyTerm : actual.getMappings().get(0).getOntologyTerms()) {
            if (ontologyTerm.getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (ontologyTerm.getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        assertEquals(1, actual.getMappingSuggestions().size());
        assertEquals("Orphanet:15", actual.getMappingSuggestions().get(0).getOntologyTerm().getCurie());

        endpoint += "?curie=MONDO:0007037";

        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk());

        entityDto = super.retrieveEntity(project.getId());
        assertEquals(1, entityDto.getMappings().size());
        assertEquals(1, entityDto.getMappings().get(0).getOntologyTerms().size());
        assertEquals("Orphanet:15", entityDto.getMappings().get(0).getOntologyTerms().get(0).getCurie());

        assertEquals(2, entityDto.getMappingSuggestions().size());
    }

    /**
     * DELETE /v1/projects/{projectId}/mappings/{mappingId}
     */
    @Test
    public void shouldDeleteMapping() throws Exception {
        EntityDto entityDto = super.retrieveEntity(project.getId());
        OntologyTermDto ontologyTermDto = null;
        for (MappingSuggestionDto mappingSuggestionDto : entityDto.getMappingSuggestions()) {
            if (mappingSuggestionDto.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                ontologyTermDto = mappingSuggestionDto.getOntologyTerm();
                break;
            }
        }
        assertNotNull(ontologyTermDto);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_MAPPINGS + "/" + orphaTermMapping.getId();
        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk());

        entityDto = super.retrieveEntity(project.getId());
        assertTrue(entityDto.getMappings().isEmpty());
        assertEquals(2, entityDto.getMappingSuggestions().size());
    }
}

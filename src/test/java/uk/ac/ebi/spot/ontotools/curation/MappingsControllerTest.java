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
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
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
        ProjectDto projectDto = super.createProject("New Project", "token1", datasources, ontologies, "efo");
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
        assertEquals("Orphanet:15", actual.getMappings().get(0).getOntologyTerm().getCurie());
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

        assertEquals(1, actual.getMappings().size());
        assertEquals("MONDO:0007037", actual.getMappings().get(0).getOntologyTerm().getCurie());
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

}

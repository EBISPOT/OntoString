package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.constants.SourceType;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class SourcesControllerTest extends IntegrationTest {

    private ProjectDto projectDto;

    @Override
    public void setup() throws Exception {
        super.setup();
        projectDto = createProject("New Project", "token1", null, null, null, 0);
    }

    /**
     * POST /v1/projects/{projectId}/sources
     */
    @Test
    public void shouldCreateSource() throws Exception {
        createSource(projectDto.getId());
    }

    /**
     * POST /v1/projects/{projectId}/sources
     */
    @Test
    public void shouldNotCreateSource() throws Exception {
        SourceCreationDto sourceCreationDto = new SourceCreationDto("Source name",
                "Description",
                null,
                SourceType.LOCAL.name());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SOURCES;

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sourceCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /v1/projects/{projectId}/sources
     */
    @Test
    public void shouldNotCreateSourceAsConsumer() throws Exception {
        SourceCreationDto sourceCreationDto = new SourceCreationDto("Source name",
                "Description",
                null,
                SourceType.LOCAL.name());

        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SOURCES;

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sourceCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/sources
     */
    @Test
    public void shouldGetSources() throws Exception {
        SourceDto sourceDto = super.createSource(projectDto.getId());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SOURCES;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<SourceDto> sourcesList = mapper.readValue(response, new TypeReference<List<SourceDto>>() {
        });
        assertEquals(1, sourcesList.size());

        SourceDto actual = sourcesList.get(0);
        assertEquals(sourceDto.getName(), actual.getName());
        assertEquals(sourceDto.getDescription(), actual.getDescription());
    }

    /**
     * GET /v1/projects/{projectId}/sources
     */
    @Test
    public void shouldNotGetSources() throws Exception {
        super.createSource(projectDto.getId());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SOURCES;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/sources/{sourceId}
     */
    @Test
    public void shouldGetSource() throws Exception {
        SourceDto sourceDto = super.createSource(projectDto.getId());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SOURCES + "/" + sourceDto.getId();
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        SourceDto actual = mapper.readValue(response, new TypeReference<SourceDto>() {
        });
        assertEquals(sourceDto.getName(), actual.getName());
        assertEquals(sourceDto.getDescription(), actual.getDescription());
    }

    /**
     * GET /v1/projects/{projectId}/sources/{sourceId}
     */
    @Test
    public void shouldNotGetSource() throws Exception {
        SourceDto sourceDto = super.createSource(projectDto.getId());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SOURCES + "/" + sourceDto.getId();
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

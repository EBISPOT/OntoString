package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectsControllerTest extends IntegrationTest {

    /**
     * POST /v1/projects
     */
    @Test
    public void shouldCreateProject() throws Exception {
        super.createProject("New Project", "token1");
    }

    /**
     * GET /v1/projects
     */
    @Test
    public void shouldGetProjects() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1");
        super.createProject("New Project 2", "token2");

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProjectDto> projectList = mapper.readValue(response, new TypeReference<List<ProjectDto>>() {
        });
        assertEquals(1, projectList.size());

        ProjectDto actual = projectList.get(0);
        assertEquals(projectDto.getName(), actual.getName());
        assertEquals(projectDto.getDescription(), actual.getDescription());
    }

    /**
     * GET /v1/projects/{projectId}
     */
    @Test
    public void shouldGetProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1");
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto actual = mapper.readValue(response, new TypeReference<ProjectDto>() {
        });
        assertEquals(projectDto.getName(), actual.getName());
        assertEquals(projectDto.getDescription(), actual.getDescription());
    }

    /**
     * PUT /v1/projects/{projectId}
     */
    @Test
    public void shouldUpdateProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1");
        ProjectDto updatedProject = new ProjectDto(projectDto.getId(),
                "New Name",
                projectDto.getDescription(),
                Arrays.asList(new String[]{"gwas"}),
                Arrays.asList(new String[]{"ordo"}),
                projectDto.getCreated());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedProject))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto actual = mapper.readValue(response, new TypeReference<ProjectDto>() {
        });
        assertEquals(updatedProject.getName(), actual.getName());
        assertEquals(updatedProject.getDescription(), actual.getDescription());
        assertEquals(updatedProject.getDatasources(), actual.getDatasources());
        assertEquals(updatedProject.getOntologies(), actual.getOntologies());
    }

    /**
     * PUT /v1/projects/{projectId}
     */
    @Test
    public void shouldNotUpdateProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1");
        ProjectDto updatedProject = new ProjectDto(projectDto.getId(),
                "New Name",
                projectDto.getDescription(),
                Arrays.asList(new String[]{"gwas"}),
                Arrays.asList(new String[]{"ordo"}),
                projectDto.getCreated());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedProject))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isForbidden());
    }

    /**
     * DELETE /v1/projects/{projectId}
     */
    @Test
    public void shouldDeleteProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1");
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(delete(endpoint)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk());

        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isNotFound());
    }
}

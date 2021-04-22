package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.ArrayList;
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
    public void shouldNotCreateProject() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS;
        ProjectCreationDto projectCreationDto = new ProjectCreationDto("New Project", "Description", 0,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), null);
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isForbidden());
    }

    /**
     * POST /v1/projects
     */
    @Test
    public void shouldCreateProject() throws Exception {
        super.createProject("New Project", user1, null, null, null, 0);
    }

    /**
     * POST /v1/projects
     */
    @Test
    public void shouldCreateProjectWithDatasources() throws Exception {
        super.createProject("New Project", user1,
                Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"}),
                Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"}),
                "efo", 0);
    }

    /**
     * GET /v1/projects
     */
    @Test
    public void shouldGetProjects() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        super.createProject("New Project 2", user2, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProjectDto> projectList = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(1, projectList.size());

        ProjectDto actual = projectList.get(0);
        assertEquals(projectDto.getName(), actual.getName());
        assertEquals(projectDto.getDescription(), actual.getDescription());
    }

    /**
     * GET /v1/projects
     */
    @Test
    public void shouldNotGetProjects() throws Exception {
        super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ProjectDto> projectList = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(0, projectList.size());
    }

    /**
     * GET /v1/projects/{projectId}
     */
    @Test
    public void shouldGetProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
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
     * GET /v1/projects/{projectId}
     */
    @Test
    public void shouldNotGetProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * PUT /v1/projects/{projectId}
     */
    @Test
    public void shouldUpdateProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        ProjectDto updatedProject = new ProjectDto(projectDto.getId(),
                "New Name",
                projectDto.getDescription(),
                null,
                0,
                projectDto.getCreated());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedProject))
                .header(IDPConstants.JWT_TOKEN, "supertoken"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto actual = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(updatedProject.getName(), actual.getName());
        assertEquals(updatedProject.getDescription(), actual.getDescription());
    }

    /**
     * PUT /v1/projects/{projectId}
     */
    @Test
    public void shouldNotUpdateProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        ProjectDto updatedProject = new ProjectDto(projectDto.getId(),
                "New Name",
                projectDto.getDescription(),
                null,
                0,
                projectDto.getCreated());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedProject))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isForbidden());
    }

    /**
     * PUT /v1/projects/{projectId}
     */
    @Test
    public void shouldNotUpdateProjectAsConsumer() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        ProjectDto updatedProject = new ProjectDto(projectDto.getId(),
                "New Name",
                projectDto.getDescription(),
                null,
                0,
                projectDto.getCreated());

        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

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
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(delete(endpoint)
                .header(IDPConstants.JWT_TOKEN, "supertoken"))
                .andExpect(status().isOk());

        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "supertoken"))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /v1/projects/{projectId}
     */
    @Test
    public void shouldNotDeleteProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(delete(endpoint)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isForbidden());
    }

    /**
     * DELETE /v1/projects/{projectId}
     */
    @Test
    public void shouldNotDeleteProjectAsConsumer() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(delete(endpoint)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isForbidden());
    }
}

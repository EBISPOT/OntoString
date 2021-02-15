package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectRole;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.UserDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectUserDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.UserDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsersControllerTest extends IntegrationTest {

    /**
     * GET /v1/projects/{projectId}/users
     */
    @Test
    public void shouldGetUsersForProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1", null, null, null);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> actual = mapper.readValue(response, new TypeReference<List<UserDto>>() {
        });

        assertEquals(1, actual.size());
        assertEquals(user1.getEmail(), actual.get(0).getEmail());
        assertEquals(user1.getName(), actual.get(0).getName());
    }

    /**
     * GET /v1/projects/{projectId}/users
     */
    @Test
    public void shouldNotGetUsersForProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1", null, null, null);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /v1/projects/{projectId}/users
     */
    @Test
    public void shouldAddUserToProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1", null, null, null);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto actual = mapper.readValue(response, new TypeReference<UserDto>() {
        });

        assertEquals(user2.getEmail(), actual.getEmail());
        assertEquals(user2.getName(), actual.getName());
        assertEquals(1, actual.getRoles().size());
        assertEquals(ProjectRole.CONTRIBUTOR.name(), actual.getRoles().get(0).getRole());

        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> actualList = mapper.readValue(response, new TypeReference<List<UserDto>>() {
        });

        assertEquals(2, actualList.size());

        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto actualProject = mapper.readValue(response, new TypeReference<ProjectDto>() {
        });
        assertEquals(projectDto.getName(), actualProject.getName());
        assertEquals(projectDto.getDescription(), actualProject.getDescription());

    }

    /**
     * PUT /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldUpdateUserToProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1", null, null, null);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto actual = mapper.readValue(response, new TypeReference<UserDto>() {
        });

        assertEquals(user2.getEmail(), actual.getEmail());
        assertEquals(user2.getName(), actual.getName());
        assertEquals(1, actual.getRoles().size());
        assertEquals(ProjectRole.CONTRIBUTOR.name(), actual.getRoles().get(0).getRole());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS + "/" + user2.getId();
        ProjectUserDto updatedProjectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONSUMER.name()}));
        response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(updatedProjectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        actual = mapper.readValue(response, new TypeReference<UserDto>() {
        });

        assertEquals(user2.getEmail(), actual.getEmail());
        assertEquals(user2.getName(), actual.getName());
        assertEquals(1, actual.getRoles().size());
        assertEquals(ProjectRole.CONSUMER.name(), actual.getRoles().get(0).getRole());
    }

    /**
     * DELETE /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldDeleteUserRolesInProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", "token1", null, null, null);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto actual = mapper.readValue(response, new TypeReference<UserDto>() {
        });

        assertEquals(user2.getEmail(), actual.getEmail());
        assertEquals(user2.getName(), actual.getName());
        assertEquals(1, actual.getRoles().size());
        assertEquals(ProjectRole.CONTRIBUTOR.name(), actual.getRoles().get(0).getRole());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS + "/" + user2.getId();
        mockMvc.perform(delete(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId();
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> actualList = mapper.readValue(response, new TypeReference<List<UserDto>>() {
        });

        assertEquals(1, actualList.size());
    }
}

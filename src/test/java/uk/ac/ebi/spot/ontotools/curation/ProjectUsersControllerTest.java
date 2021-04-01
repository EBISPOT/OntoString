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

public class ProjectUsersControllerTest extends IntegrationTest {

    /**
     * GET /v1/projects/{projectId}/users
     */
    @Test
    public void shouldGetUsersForProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<UserDto> actual = mapper.readValue(response, new TypeReference<>() {
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
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/users
     */
    @Test
    public void shouldNotGetUsersForProjectAsContributor() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;

        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONTRIBUTOR}));
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/users
     */
    @Test
    public void shouldNotGetUsersForProjectAsConsumer() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;

        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));
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
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

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

        UserDto actual = mapper.readValue(response, new TypeReference<>() {
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

        List<UserDto> actualList = mapper.readValue(response, new TypeReference<>() {
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

        ProjectDto actualProject = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(projectDto.getName(), actualProject.getName());
        assertEquals(projectDto.getDescription(), actualProject.getDescription());

    }

    /**
     * POST /v1/projects/{projectId}/users
     */
    @Test
    public void shouldNotAddUserToProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /v1/projects/{projectId}/users
     */
    @Test
    public void shouldNotAddUserToProjectAsContributor() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));

        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONTRIBUTOR}));

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /v1/projects/{projectId}/users
     */
    @Test
    public void shouldNotAddUserToProjectAsConsumer() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));

        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * PUT /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldUpdateUserToProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

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

        UserDto actual = mapper.readValue(response, new TypeReference<>() {
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

        actual = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(user2.getEmail(), actual.getEmail());
        assertEquals(user2.getName(), actual.getName());
        assertEquals(1, actual.getRoles().size());
        assertEquals(ProjectRole.CONSUMER.name(), actual.getRoles().get(0).getRole());
    }

    /**
     * PUT /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldNotUpdateUserToProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * PUT /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldNotUpdateUserToProjectAsContributor() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONTRIBUTOR}));

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * PUT /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldNotUpdateUserToProjectAsConsumer() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }


    /**
     * DELETE /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldDeleteUserRolesInProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

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

        UserDto actual = mapper.readValue(response, new TypeReference<>() {
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

        List<UserDto> actualList = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(1, actualList.size());
    }

    /**
     * DELETE /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldNotDeleteUserRolesInProject() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldNotDeleteUserRolesInProjectAsContributor() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONTRIBUTOR}));
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * DELETE /v1/projects/{projectId}/users/{userId}
     */
    @Test
    public void shouldNotDeleteUserRolesInProjectAsConsumer() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_USERS;
        ProjectUserDto projectUserDto = new ProjectUserDto(UserDtoAssembler.assemble(user2), Arrays.asList(new String[]{ProjectRole.CONTRIBUTOR.name()}));
        userService.addUserToProject(super.user2, projectDto.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectUserDto))
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

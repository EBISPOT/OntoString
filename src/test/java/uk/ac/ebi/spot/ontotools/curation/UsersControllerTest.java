package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.RestResponsePage;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.UserCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.UserDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class UsersControllerTest extends IntegrationTest {

    /**
     * GET /v1/users
     */
    @Test
    public void shouldGetUsers() throws Exception {
        super.createProject("New Project", user1, null, null, null, 0, null);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_USERS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponsePage<UserDto> actualPage = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(4, actualPage.getContent().size());
    }

    /**
     * GET /v1/users
     */
    @Test
    public void shouldNotGetUsers() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_USERS;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    /**
     * POST /v1/users
     */
    @Test
    public void shouldCreateUser() throws Exception {
        super.createProject("New Project", user1, null, null, null, 0, null);
        UserCreationDto userCreationDto = new UserCreationDto("Me", "me@me.com");
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_USERS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1")
                .content(mapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        UserDto actual = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(userCreationDto.getName(), actual.getName());
        assertEquals(userCreationDto.getEmail(), actual.getEmail());
    }

    /**
     * POST /v1/users
     */
    @Test
    public void shouldNotCreateUser() throws Exception {
        UserCreationDto userCreationDto = new UserCreationDto("Me", "me@me.com");
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_USERS;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1")
                .content(mapper.writeValueAsString(userCreationDto)))
                .andExpect(status().isForbidden());
    }
}

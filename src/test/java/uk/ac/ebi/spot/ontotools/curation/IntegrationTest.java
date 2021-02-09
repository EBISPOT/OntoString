package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.AuthToken;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.repository.AuthTokenRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.UserRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.ArrayList;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
public abstract class IntegrationTest {

    @Configuration
    public static class MockTaskExecutorConfig {

        @Bean
        public TaskExecutor taskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Autowired
    private MongoTemplate mongoTemplate;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthTokenRepository authTokenRepository;

    protected MockMvc mockMvc;

    protected ObjectMapper mapper;

    protected User user1;

    protected User user2;

    @Before
    public void setup() throws Exception {
        mongoTemplate.getDb().drop();
        mapper = new ObjectMapper();
        mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        userRepository.insert(new User(null, "Robot User", "ontotools-curator@ebi.ac.uk", new ArrayList<>(), true));

        user1 = userRepository.insert(new User(null, "Test User 1", "test1@test.com", new ArrayList<>(), false));
        authTokenRepository.insert(new AuthToken(null, "token1", "test1@test.com"));

        user2 = userRepository.insert(new User(null, "Test User 2", "test2@test.com", new ArrayList<>(), false));
        authTokenRepository.insert(new AuthToken(null, "token2", "test2@test.com"));

        userRepository.insert(new User(null, "Super User", "super@test.com", new ArrayList<>(), true));
        authTokenRepository.insert(new AuthToken(null, "supertoken", "super@test.com"));
    }

    protected ProjectDto createProject(String name, String token) throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS;
        ProjectCreationDto projectCreationDto = new ProjectCreationDto(name, "Description", null, null, "EFO");

        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectCreationDto))
                .header(IDPConstants.JWT_TOKEN, token))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto actual = mapper.readValue(response, new TypeReference<ProjectDto>() {
        });
        assertEquals(projectCreationDto.getName(), actual.getName());
        assertEquals(projectCreationDto.getDescription(), actual.getDescription());
        assertNotNull(actual.getDatasources());
        assertNotNull(actual.getOntologies());
        assertTrue(actual.getDatasources().isEmpty());
        assertTrue(actual.getOntologies().isEmpty());
        assertEquals("EFO", actual.getPreferredMappingOntology());
        return actual;
    }

}

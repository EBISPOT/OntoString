package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
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
import uk.ac.ebi.spot.ontotools.curation.constants.*;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.AuthToken;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.repository.*;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.service.MatchmakerService;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.service.ZoomaService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import javax.annotation.PreDestroy;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@ContextConfiguration(classes = {Application.class})
public abstract class IntegrationTest {

    @Configuration
    public static class MockTaskExecutorConfig {

        @Bean
        public TaskExecutor applicationTaskExecutor() {
            return new SyncTaskExecutor();
        }
    }

    @Configuration
    public static class MockMatchmakerServiceConfig {

        @Bean
        public MatchmakerService matchmakerService() {
            return mock(MatchmakerService.class);
        }
    }

    @Configuration
    public static class MockZoomaServiceConfig {

        @Bean
        public ZoomaService zoomaService() {
            return mock(ZoomaService.class);
        }
    }

    @Configuration
    public static class MockOLSServiceConfig {

        @Bean
        public OLSService olsService() {
            return mock(OLSService.class);
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

    @Autowired
    protected OntologyTermRepository ontologyTermRepository;

    @Autowired
    protected MappingSuggestionRepository mappingSuggestionRepository;

    @Autowired
    protected MappingRepository mappingRepository;

    @Autowired
    private EntityRepository entityRepository;

    @Autowired
    protected UserService userService;

    protected MockMvc mockMvc;

    protected ObjectMapper mapper;

    protected User user1;

    protected User user2;

    protected Entity entity;

    protected Mapping orphaTermMapping;

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

    @PreDestroy
    public void destroy() {
        mongoTemplate.getDb().drop();
    }

    protected ProjectDto createProject(String name, String token,
                                       List<String> datasources, List<String> ontologies,
                                       String preferredMappingOntology, int noReviewsRequired) throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS;

        ProjectCreationDto projectCreationDto = new ProjectCreationDto(name, "Description", noReviewsRequired,
                datasources != null ? datasources : new ArrayList<>(),
                ontologies != null ? ontologies : new ArrayList<>(),
                preferredMappingOntology != null ? Arrays.asList(new String[]{preferredMappingOntology}) : new ArrayList<>());

        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(projectCreationDto))
                .header(IDPConstants.JWT_TOKEN, token))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto actual = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(projectCreationDto.getName(), actual.getName());
        assertEquals(projectCreationDto.getDescription(), actual.getDescription());
        assertEquals(1, actual.getContexts().size());
        assertEquals(CurationConstants.CONTEXT_DEFAULT, actual.getContexts().get(0).getName());
        assertNotNull(actual.getContexts().get(0).getDatasources());
        assertNotNull(actual.getContexts().get(0).getOntologies());

        if (datasources == null) {
            assertTrue(actual.getContexts().get(0).getDatasources().isEmpty());
        } else {
            assertEquals(datasources.size(), actual.getContexts().get(0).getDatasources().size());
        }
        if (ontologies == null) {
            assertTrue(actual.getContexts().get(0).getOntologies().isEmpty());
        } else {
            assertEquals(ontologies.size(), actual.getContexts().get(0).getOntologies().size());
        }
        if (preferredMappingOntology != null) {
            assertEquals(preferredMappingOntology, actual.getContexts().get(0).getPreferredMappingOntologies().get(0));
        }
        return actual;
    }

    protected SourceDto createSource(String projectId) throws Exception {
        SourceCreationDto sourceCreationDto = new SourceCreationDto("Source name",
                "Description",
                null,
                SourceType.LOCAL.name());
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectId + CurationConstants.API_SOURCES;

        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(sourceCreationDto))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        SourceDto actual = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(sourceCreationDto.getName(), actual.getName());
        assertEquals(sourceCreationDto.getDescription(), actual.getDescription());
        assertEquals(sourceCreationDto.getType(), actual.getType());
        return actual;
    }

    protected void createEntityTestData(String sourceId, String projectId, User user) {
        Provenance provenance = new Provenance(user.getName(), user.getEmail(), DateTime.now());
        entity = entityRepository.insert(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceId, projectId, null, provenance, EntityStatus.AUTO_MAPPED));

        OntologyTerm orphaTerm = ontologyTermRepository.insert(new OntologyTerm(null, "Orphanet:15", "http://www.orpha.net/ORDO/Orphanet_15",
                DigestUtils.sha256Hex("http://www.orpha.net/ORDO/Orphanet_15"), "Achondroplasia", TermStatus.CURRENT.name(), null, null));

        OntologyTerm mondoTerm = ontologyTermRepository.insert(new OntologyTerm(null, "MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037",
                DigestUtils.sha256Hex("http://purl.obolibrary.org/obo/MONDO_0007037"), "Achondroplasia", TermStatus.NEEDS_IMPORT.name(), null, null));

        mappingSuggestionRepository.insert(new MappingSuggestion(null, entity.getId(), orphaTerm.getId(), projectId, provenance, null));
        mappingSuggestionRepository.insert(new MappingSuggestion(null, entity.getId(), mondoTerm.getId(), projectId, provenance, null));
        orphaTermMapping = mappingRepository.insert(new Mapping(null, entity.getId(), Arrays.asList(new String[]{orphaTerm.getId()}),
                projectId, false, new ArrayList<>(), new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(), provenance, null));
    }

    protected MappingDto retrieveMapping(String projectId) throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectId +
                CurationConstants.API_ENTITIES + "/" + entity.getId() + CurationConstants.API_MAPPING;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        MappingDto actual = mapper.readValue(response, new TypeReference<>() {
        });
        return actual;
    }
}

package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.*;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.ProvenanceDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ReviewDto;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class MappingReviewsControllerTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MappingService mappingService;

    private Project project;

    private SourceDto sourceDto;

    @Override
    public void setup() throws Exception {
        super.setup();
        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 3);
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
        sourceDto = super.createSource(project.getId());

        super.createEntityTestData(sourceDto.getId(), project.getId(), user1);
    }

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldCreateReview() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content("New review")
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ReviewDto reviewDto = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals("New review", reviewDto.getComment());
        Mapping mapping = mappingService.retrieveMappingById(mappingDto.getId());
        assertEquals(1, mapping.getReviews().size());
        assertEquals("New review", mapping.getReviews().get(0).getComment());
        assertFalse(mapping.isReviewed());
        assertEquals(MappingStatus.REVIEW_IN_PROGRESS.name(), mapping.getStatus());


        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actualEntity = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(1, actualEntity.getAuditTrail().size());
        assertEquals(AuditEntryConstants.REVIEWED.name(), actualEntity.getAuditTrail().get(0).getAction());
    }

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldCreateReviewedMapping() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;

        for (int i = 0; i < 3; i++) {
            mockMvc.perform(post(endpoint)
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("New review")
                    .header(IDPConstants.JWT_TOKEN, "token1"))
                    .andExpect(status().isCreated())
                    .andReturn()
                    .getResponse()
                    .getContentAsString();
        }

        Mapping mapping = mappingService.retrieveMappingById(mappingDto.getId());
        assertTrue(mapping.isReviewed());
        assertEquals(MappingStatus.REQUIRED_REVIEWS_REACHED.name(), mapping.getStatus());
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldGetReviews() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        mappingService.addReviewToMapping(mappingDto.getId(), "New review", 3, ProvenanceDtoAssembler.disassemble(mappingDto.getCreated()));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ReviewDto> reviewDtos = mapper.readValue(response, new TypeReference<List<ReviewDto>>() {
        });
        assertEquals(1, reviewDtos.size());
        assertEquals("New review", reviewDtos.get(0).getComment());
    }

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldNotCreateReview() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content("New review")
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldNotGetReviews() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        mappingService.addReviewToMapping(mappingDto.getId(), "New review", 3, ProvenanceDtoAssembler.disassemble(mappingDto.getCreated()));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldNotCreateReviewAsConsumer() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        userService.addUserToProject(super.user2, project.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content("New review")
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/reviews
     */
    @Test
    public void shouldNotGetReviewsAsConsumer() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        mappingService.addReviewToMapping(mappingDto.getId(), "New review", 3, ProvenanceDtoAssembler.disassemble(mappingDto.getCreated()));
        userService.addUserToProject(super.user2, project.getId(), Arrays.asList(new ProjectRole[]{ProjectRole.CONSUMER}));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_REVIEWS;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

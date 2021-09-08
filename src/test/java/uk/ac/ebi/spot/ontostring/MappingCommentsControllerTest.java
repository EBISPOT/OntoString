package uk.ac.ebi.spot.ontostring;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontostring.rest.assembler.ProvenanceDtoAssembler;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.CommentDto;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontostring.service.MappingService;
import uk.ac.ebi.spot.ontostring.service.ProjectService;
import uk.ac.ebi.spot.ontostring.service.UserService;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.IDPConstants;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class MappingCommentsControllerTest extends IntegrationTest {

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
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0, null);
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
        sourceDto = super.createSource(project.getId());

        super.createEntityTestData(sourceDto.getId(), project.getId(), user1);
    }

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/comments
     */
    @Test
    public void shouldCreateComment() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_COMMENTS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content("New comment")
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        CommentDto commentDto = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals("New comment", commentDto.getBody());
        Mapping mapping = mappingService.retrieveMappingById(mappingDto.getId());
        assertEquals(1, mapping.getComments().size());
        assertEquals("New comment", mapping.getComments().get(0).getBody());
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/comments
     */
    @Test
    public void shouldGetComments() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        mappingService.addCommentToMapping(mappingDto.getId(), "New comment", ProvenanceDtoAssembler.disassemble(mappingDto.getCreated()));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_COMMENTS;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<CommentDto> commentDtos = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(1, commentDtos.size());
        assertEquals("New comment", commentDtos.get(0).getBody());
    }

    /**
     * POST /v1/projects/{projectId}/mappings/{mappingId}/comments
     */
    @Test
    public void shouldNotCreateComment() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_COMMENTS;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content("New comment")
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/mappings/{mappingId}/comments
     */
    @Test
    public void shouldNotGetComments() throws Exception {
        MappingDto mappingDto = super.retrieveMapping(project.getId());
        mappingService.addCommentToMapping(mappingDto.getId(), "New comment", ProvenanceDtoAssembler.disassemble(mappingDto.getCreated()));

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_MAPPINGS + "/" + mappingDto.getId() + CurationConstants.API_COMMENTS;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

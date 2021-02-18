package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class OntologyTermControllerTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    private Project project;

    @Override
    public void setup() throws Exception {
        super.setup();

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
        ProjectDto projectDto = super.createProject("New Project", "token1", datasources, ontologies, "efo");
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
    }

    /**
     * POST /v1/projects/{projectId}/ontology-terms
     */
    @Test
    public void shouldCreateOntologyTerm() throws Exception {
        OntologyTermDto toCreate = new OntologyTermDto("MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037",
                "Achondroplasia", null, null, null);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ONTOLOGY_TERMS;
        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toCreate))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OntologyTermDto actual = mapper.readValue(response, new TypeReference<OntologyTermDto>() {
        });

        assertEquals(toCreate.getLabel(), actual.getLabel());
        assertEquals(toCreate.getCurie(), actual.getCurie());
        assertEquals(toCreate.getIri(), actual.getIri());
        assertEquals(TermStatus.NEEDS_IMPORT.toString(), actual.getStatus());

    }
}

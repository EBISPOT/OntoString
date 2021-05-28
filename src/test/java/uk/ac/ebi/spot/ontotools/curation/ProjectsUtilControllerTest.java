package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.OLSQueryResultsType;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryResultDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectsUtilControllerTest extends IntegrationTest {

    /**
     * GET /v1/projects/{projectId}/searchOLS?query=<query>
     */
    @Test
    public void shouldQueryOLS() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, null, null, null, 0, null);
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + projectDto.getId() + CurationConstants.API_SEARCH_OLS +
                "?" + CurationConstants.PARAM_QUERY + "=diabetes&" + CurationConstants.PARAM_CONTEXT + "=" + CurationConstants.CONTEXT_DEFAULT;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OLSQueryResultDto result = mapper.readValue(response, new TypeReference<>() {
        });
        assertFalse(result.getResults().isEmpty());
        assertTrue(result.getResults().containsKey(OLSQueryResultsType.ALL.name()));

        assertEquals(10, result.getResults().get(OLSQueryResultsType.ALL.name()).size());
        List<String> efoUris = new ArrayList<>();
        for (OLSQueryDocDto olsQueryDocDto : result.getResults().get(OLSQueryResultsType.ALL.name())) {
            if (olsQueryDocDto.getOntologyName().equalsIgnoreCase("efo")) {
                efoUris.add(olsQueryDocDto.getCurie());
            }
        }
        assertTrue(efoUris.contains("EFO:0000400"));
    }

}

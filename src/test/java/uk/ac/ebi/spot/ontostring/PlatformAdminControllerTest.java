package uk.ac.ebi.spot.ontostring;

import com.fasterxml.jackson.core.type.TypeReference;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontostring.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontostring.rest.dto.config.ExternalServiceConfigDto;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.IDPConstants;
import uk.ac.ebi.spot.ontostring.repository.ExternalServiceConfigRepository;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class PlatformAdminControllerTest extends IntegrationTest {

    @Autowired
    private ExternalServiceConfigRepository externalServiceConfigRepository;

    @Override
    public void setup() throws Exception {
        super.setup();
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
    }

    /**
     * PUT /v1/platform-admin
     */
    @Test
    public void shouldUpdateConfig() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PLATFORM_ADMIN;
        Map<String, String> newMap = new HashMap<>();
        newMap.put("orphanet", "ordo");
        newMap.put("efox", "efo");
        ExternalServiceConfigDto toUpdate = new ExternalServiceConfigDto("OLS", newMap);
        String response = mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toUpdate))
                .header(IDPConstants.JWT_TOKEN, "supertoken"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ExternalServiceConfigDto actual = mapper.readValue(response, new TypeReference<ExternalServiceConfigDto>() {
        });
        assertEquals("OLS", actual.getServiceName());
        assertEquals(newMap, actual.getAliases());
    }

    /**
     * GET /v1/platform-admin
     */
    @Test
    public void shouldGetConfigs() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PLATFORM_ADMIN;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "supertoken"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<ExternalServiceConfigDto> externalServiceConfigDtos = mapper.readValue(response, new TypeReference<List<ExternalServiceConfigDto>>() {
        });
        assertEquals(1, externalServiceConfigDtos.size());

        ExternalServiceConfigDto actual = externalServiceConfigDtos.get(0);
        assertEquals("OLS", actual.getServiceName());
        Map<String, String> expectedMap = new HashMap<>();
        expectedMap.put("orphanet", "ordo");
        assertEquals(expectedMap, actual.getAliases());
    }

    /**
     * GET /v1/platform-admin
     */
    @Test
    public void shouldNotGetConfigs() throws Exception {
        mockMvc.perform(get(GeneralCommon.API_V1 + CurationConstants.API_PLATFORM_ADMIN)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isForbidden());
    }

    /**
     * PUT /v1/platform-admin
     */
    @Test
    public void shouldNotUpdateConfig() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PLATFORM_ADMIN;
        Map<String, String> newMap = new HashMap<>();
        newMap.put("orphanet", "ordo");
        newMap.put("efox", "efo");
        ExternalServiceConfigDto toUpdate = new ExternalServiceConfigDto("OLS", newMap);
        mockMvc.perform(put(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(toUpdate))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isForbidden());
    }
}

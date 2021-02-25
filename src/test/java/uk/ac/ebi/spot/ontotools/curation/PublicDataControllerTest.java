package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.ProjectExportRequestStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectExportRequest;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.ProjectExportRequestRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectExportStatusDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.service.EntityService;
import uk.ac.ebi.spot.ontotools.curation.service.MatchmakerService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class PublicDataControllerTest extends IntegrationTest {

    @Autowired
    private ExternalServiceConfigRepository externalServiceConfigRepository;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private MatchmakerService matchmakerService;

    @Autowired
    private ProjectExportRequestRepository projectExportRequestRepository;

    private Project project;

    private SourceDto sourceDto;

    @Override
    public void setup() throws Exception {
        super.setup();
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OXO", Arrays.asList(new String[]{"ordo::orphanet"})));

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});

        ProjectDto projectDto = super.createProject("New Project", "token1", datasources, ontologies, "efo", 0);
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
        sourceDto = super.createSource(project.getId());
        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());

        /**
         * Other examples:
         * - Hemochromatosis type 1
         * - Retinal dystrophy
         */
        entity = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10), sourceDto.getId(), project.getId(), provenance, EntityStatus.UNMAPPED));
        matchmakerService.runMatchmaking(sourceDto.getId(), project);
    }


    /**
     * POST - GET /public/v1/projects/{projectId}/export
     */
    @Test
    public void shouldCreateExportRequest() throws Exception {
        String endpoint = GeneralCommon.API_PUBLIC + GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_EXPORT;
        String requestId = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();


        endpoint = endpoint + "/" + requestId;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();


        ProjectExportStatusDto actual = mapper.readValue(response, new TypeReference<ProjectExportStatusDto>() {
        });
        assertEquals(requestId, actual.getRequestId());

        Thread.sleep(10000);
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        actual = mapper.readValue(response, new TypeReference<ProjectExportStatusDto>() {
        });
        assertEquals(requestId, actual.getRequestId());
        assertEquals(ProjectExportRequestStatus.FINALIZED.name(), actual.getStatus());

        Optional<ProjectExportRequest> projectExportRequestOptional = projectExportRequestRepository.findByRequestId(requestId);
        assertTrue(projectExportRequestOptional.isPresent());
        assertNotNull(projectExportRequestOptional.get().getFileId());

        endpoint = endpoint + CurationConstants.API_DOWNLOAD;
        MockHttpServletResponse dataResponse = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse();

        byte[] payload = dataResponse.getContentAsByteArray();
        assertTrue(payload.length != 0);
        assertEquals("attachment; filename=" + project.getId() + ".zip", dataResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));

        ZipInputStream zi = null;
        List<ZipEntry> entries = new ArrayList<>();
        try {
            zi = new ZipInputStream(new ByteArrayInputStream(payload));

            ZipEntry zipEntry = null;
            while ((zipEntry = zi.getNextEntry()) != null) {
                entries.add(zipEntry);
            }
        } finally {
            if (zi != null) {
                zi.close();
            }
        }

        assertFalse(entries.isEmpty());
        assertEquals(project.getId() + ".json", entries.get(0).getName());
        super.destroy();
    }
}

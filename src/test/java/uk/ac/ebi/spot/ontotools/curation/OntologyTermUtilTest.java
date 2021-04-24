package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.codec.digest.DigestUtils;
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
import uk.ac.ebi.spot.ontotools.curation.constants.IDPConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.RestResponsePage;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ActionOntologyTermsDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ExportOntologyTermsDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ExtendedOntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermStatsDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.service.UserService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class OntologyTermUtilTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

    @Autowired
    private MappingService mappingService;

    private Project project;

    private SourceDto sourceDto;

    private Mapping orphaMapping;

    private Mapping mondoMapping;

    private OntologyTerm orphaTerm;

    private OntologyTerm mondoTerm;

    private Entity entity1;

    @Override
    public void setup() throws Exception {
        super.setup();

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0, null);
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
        sourceDto = super.createSource(project.getId());

        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());
        entity1 = entityRepository.insert(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), null, provenance, EntityStatus.AUTO_MAPPED));

        orphaTerm = ontologyTermRepository.insert(new OntologyTerm(null, "Orphanet:15", "http://www.orpha.net/ORDO/Orphanet_15",
                DigestUtils.sha256Hex("http://www.orpha.net/ORDO/Orphanet_15"), "Achondroplasia",
                Arrays.asList(new OntologyTermContext[]{
                        new OntologyTermContext(entity1.getProjectId(), entity1.getContext(), TermStatus.NEEDS_IMPORT.name())
                }), null, null));

        mondoTerm = ontologyTermRepository.insert(new OntologyTerm(null, "MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037",
                DigestUtils.sha256Hex("http://purl.obolibrary.org/obo/MONDO_0007037"), "Achondroplasia",
                Arrays.asList(new OntologyTermContext[]{
                        new OntologyTermContext(entity1.getProjectId(), entity1.getContext(), TermStatus.NEEDS_CREATION.name())
                }), null, null));

        orphaMapping = mappingService.createMapping(entity1, Arrays.asList(new OntologyTerm[]{orphaTerm}), provenance);
        mondoMapping = mappingService.createMapping(entity1, Arrays.asList(new OntologyTerm[]{mondoTerm}), provenance);
    }

    /**
     * POST /v1/projects/{projectId}/ontology-terms/action
     */
    @Test
    public void shouldActionOntologyTerms() throws Exception {
        ActionOntologyTermsDto payload = new ActionOntologyTermsDto(TermStatus.NEEDS_IMPORT.name(),
                CurationConstants.CONTEXT_DEFAULT,
                "New Comment");

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ONTOLOGY_TERMS + CurationConstants.API_ACTION;
        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated());

        orphaTerm = ontologyTermRepository.findById(orphaTerm.getId()).get();
        assertEquals(TermStatus.AWAITING_IMPORT.name(), orphaTerm.getContexts().get(0).getStatus());
        orphaMapping = mappingRepository.findById(orphaMapping.getId()).get();
        assertEquals(1, orphaMapping.getComments().size());
        assertEquals("New Comment", orphaMapping.getComments().get(0).getBody());

        mondoTerm = ontologyTermRepository.findById(mondoTerm.getId()).get();
        assertEquals(TermStatus.NEEDS_CREATION.name(), mondoTerm.getContexts().get(0).getStatus());
        mondoMapping = mappingRepository.findById(mondoMapping.getId()).get();
        assertEquals(0, mondoMapping.getComments().size());

        payload = new ActionOntologyTermsDto(TermStatus.NEEDS_CREATION.name(),
                CurationConstants.CONTEXT_DEFAULT,
                "New Comment");

        mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated());

        orphaTerm = ontologyTermRepository.findById(orphaTerm.getId()).get();
        assertEquals(TermStatus.AWAITING_IMPORT.name(), orphaTerm.getContexts().get(0).getStatus());
        orphaMapping = mappingRepository.findById(orphaMapping.getId()).get();
        assertEquals(1, orphaMapping.getComments().size());
        assertEquals("New Comment", orphaMapping.getComments().get(0).getBody());

        mondoTerm = ontologyTermRepository.findById(mondoTerm.getId()).get();
        assertEquals(TermStatus.AWAITING_CREATION.name(), mondoTerm.getContexts().get(0).getStatus());
        mondoMapping = mappingRepository.findById(mondoMapping.getId()).get();
        assertEquals(1, mondoMapping.getComments().size());
        assertEquals("New Comment", mondoMapping.getComments().get(0).getBody());
    }


    /**
     * POST /v1/projects/{projectId}/ontology-terms/export
     */
    @Test
    public void shouldExportOntologyTerms() throws Exception {
        ExportOntologyTermsDto payload = new ExportOntologyTermsDto(TermStatus.NEEDS_IMPORT.name(),
                CurationConstants.CONTEXT_DEFAULT);

        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ONTOLOGY_TERMS + CurationConstants.API_EXPORT;

        MockHttpServletResponse dataResponse = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(mapper.writeValueAsString(payload))
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse();

        byte[] data = dataResponse.getContentAsByteArray();
        assertTrue(data.length != 0);
        assertEquals("attachment; filename=terms_" + project.getId() + "_" + CurationConstants.CONTEXT_DEFAULT + ".csv", dataResponse.getHeader(HttpHeaders.CONTENT_DISPOSITION));

        String sContent = new String(data, StandardCharsets.UTF_8);
        String[] lines = sContent.split("\n");
        assertEquals(2, lines.length);
        assertTrue(sContent.contains(orphaTerm.getIri()));
    }

    /**
     * GET /v1/projects/{projectId}/ontology-terms?status=<STATUS>&context=<CONTEXT>
     */
    @Test
    public void shouldGetOntologyTerms() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ONTOLOGY_TERMS + "?context=" + CurationConstants.CONTEXT_DEFAULT + "&status=" + TermStatus.NEEDS_IMPORT.name();

        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponsePage<ExtendedOntologyTermDto> actual = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals(1, actual.getTotalElements());
        assertEquals(orphaTerm.getCurie(), actual.getContent().get(0).getOntologyTerm().getCurie());
        assertEquals(1, actual.getContent().get(0).getEntities().size());
        assertEquals(entity1.getId(), actual.getContent().get(0).getEntities().get(0).getId());
        assertEquals(entity1.getName(), actual.getContent().get(0).getEntities().get(0).getName());
    }


    /**
     * GET /v1/projects/{projectId}/ontology-terms-stats?context=<CONTEXT>
     */
    @Test
    public void shouldGetOntologyTermsStats() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ONTOLOGY_TERMS_STATS + "?context=" + CurationConstants.CONTEXT_DEFAULT;

        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        OntologyTermStatsDto actual = mapper.readValue(response, new TypeReference<>() {
        });

        assertFalse(actual.getStats().isEmpty());
        assertEquals(1, actual.getStats().get(TermStatus.NEEDS_CREATION.name()).intValue());
        assertEquals(1, actual.getStats().get(TermStatus.NEEDS_IMPORT.name()).intValue());
    }
}

package uk.ac.ebi.spot.ontostring;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.Provenance;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontostring.rest.dto.RestResponsePage;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.MappingSuggestionDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectContextDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontostring.service.ProjectService;
import uk.ac.ebi.spot.ontostring.service.UserService;
import uk.ac.ebi.spot.ontostring.system.GeneralCommon;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.constants.IDPConstants;
import uk.ac.ebi.spot.ontostring.constants.MappingStatus;
import uk.ac.ebi.spot.ontostring.rest.dto.*;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class EntityControllerTest extends IntegrationTest {

    @Autowired
    private UserService userService;

    @Autowired
    private ProjectService projectService;

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
     * GET /v1/projects/{projectId}/entities
     */
    @Test
    public void shouldGetEntities() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponsePage<EntityDto> entitiesPage = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(1, entitiesPage.getTotalElements());

        EntityDto actual = entitiesPage.getContent().get(0);
        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.AUTO_MAPPED.name(), actual.getMappingStatus());
        assertEquals(CurationConstants.CONTEXT_DEFAULT, actual.getContext());

        assertNotNull(actual.getMapping());
        Assert.assertEquals("Orphanet:15", actual.getMapping().getOntologyTerms().get(0).getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.getMapping().getStatus());

        assertEquals(2, actual.getMappingSuggestions().size());
        int foundCuries = 0;
        for (MappingSuggestionDto mappingSuggestion : actual.getMappingSuggestions()) {
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        Assert.assertEquals(sourceDto.getId(), actual.getSource().getId());
    }

    /**
     * GET /v1/projects/{projectId}/entities?search=<search>
     */
    @Test
    public void shouldGetEntitiesByPrefixSearch() throws Exception {
        super.entityRepository.insert(new Entity(null, "Achonparestesia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), null,
                new Provenance(user1.getName(), user1.getEmail(), DateTime.now()), EntityStatus.AUTO_MAPPED));

        String prefix = "chon";
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES +
                "?" + CurationConstants.PARAM_SEARCH + "=" + prefix;
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponsePage<EntityDto> entitiesPage = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, entitiesPage.getTotalElements());

        prefix = "achond";
        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES +
                "?" + CurationConstants.PARAM_SEARCH + "=" + prefix;
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        entitiesPage = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(1, entitiesPage.getTotalElements());
        EntityDto actual = entitiesPage.getContent().get(0);
        assertEquals("Achondroplasia", actual.getName());
    }

    /**
     * GET /v1/projects/{projectId}/entities?context=<CONTEXT>
     */
    @Test
    public void shouldGetEntitiesByContext() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_CONTEXTS;
        ProjectContextDto newProjectContextDto = new ProjectContextDto("species_mouse", "",
                Arrays.asList(new String[]{"sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"}),
                Arrays.asList(new String[]{"efo", "mondo", "mp"}),
                Arrays.asList(new String[]{"efo"}), null);

        String response = mockMvc.perform(post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1")
                .content(super.mapper.writeValueAsString(newProjectContextDto)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString();

        ProjectDto projectResponse = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, projectResponse.getContexts().size());

        super.entityRepository.insert(new Entity(null, "Achonparestesia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), null,
                new Provenance(user1.getName(), user1.getEmail(), DateTime.now()), EntityStatus.AUTO_MAPPED));

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES +
                "?" + CurationConstants.PARAM_CONTEXT + "=species_mouse";
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        RestResponsePage<EntityDto> entitiesPage = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(0, entitiesPage.getTotalElements());

        endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES +
                "?" + CurationConstants.PARAM_CONTEXT + "=DEFAULT";
        response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        entitiesPage = mapper.readValue(response, new TypeReference<>() {
        });
        assertEquals(2, entitiesPage.getTotalElements());
    }

    /**
     * GET /v1/projects/{projectId}/entities/{entityId}
     */
    @Test
    public void shouldGetEntity() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token1"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        EntityDto actual = mapper.readValue(response, new TypeReference<>() {
        });

        assertEquals("Achondroplasia", actual.getName());
        assertEquals(EntityStatus.AUTO_MAPPED.name(), actual.getMappingStatus());
        assertEquals(CurationConstants.CONTEXT_DEFAULT, actual.getContext());

        assertNotNull(actual.getMapping());
        Assert.assertEquals("Orphanet:15", actual.getMapping().getOntologyTerms().get(0).getCurie());
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), actual.getMapping().getStatus());

        assertEquals(2, actual.getMappingSuggestions().size());
        int foundCuries = 0;
        for (MappingSuggestionDto mappingSuggestion : actual.getMappingSuggestions()) {
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("Orphanet:15")) {
                foundCuries++;
            }
            if (mappingSuggestion.getOntologyTerm().getCurie().equalsIgnoreCase("MONDO:0007037")) {
                foundCuries++;
            }
        }

        assertEquals(2, foundCuries);
        Assert.assertEquals(sourceDto.getId(), actual.getSource().getId());
    }

    /**
     * GET /v1/projects/{projectId}/entities
     */
    @Test
    public void shouldNotGetEntities() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() + CurationConstants.API_ENTITIES;
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }

    /**
     * GET /v1/projects/{projectId}/entities/{entityId}
     */
    @Test
    public void shouldNotGetEntity() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_PROJECTS + "/" + project.getId() +
                CurationConstants.API_ENTITIES + "/" + entity.getId();
        mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .header(IDPConstants.JWT_TOKEN, "token2"))
                .andExpect(status().isNotFound());
    }
}

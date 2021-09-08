package uk.ac.ebi.spot.ontostring;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.ProjectContext;
import uk.ac.ebi.spot.ontostring.domain.Provenance;
import uk.ac.ebi.spot.ontostring.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontostring.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontostring.service.*;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.constants.TermStatus;
import uk.ac.ebi.spot.ontostring.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontostring.service.*;

import java.util.*;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class,
        IntegrationTest.MockZoomaServiceConfig.class,
        IntegrationTest.MockOLSServiceConfig.class})
public class OntoTermContextStatusTest extends IntegrationTest {

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

    private List<String> datasources;

    private List<String> ontologies;

    @Autowired
    private ZoomaService zoomaService;

    @Autowired
    private OLSService olsService;

    @Override
    public void setup() throws Exception {
        super.setup();
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OXO", Arrays.asList(new String[]{"ordo::orphanet"})));

        this.datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        this.ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
    }

    /**
     * Test case:
     * - Match 2 entities from 2 projects annotated with the same term.
     * -- Expected: term should have 2 contexts
     */
    @Test
    public void testCase1() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, datasources, ontologies, "efo", 0, null);

        user1 = userService.findByEmail(user1.getEmail());
        Project project1 = projectService.retrieveProject(projectDto.getId(), user1);
        SourceDto sourceDto1 = super.createSource(project1.getId());
        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());
        Entity entity1 = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto1.getId(), project1.getId(), 10, provenance, EntityStatus.UNMAPPED));

        projectDto = super.createProject("New Project 2", user1, datasources, ontologies, "efo", 0, null);
        user1 = userService.findByEmail(user1.getEmail());
        Project project2 = projectService.retrieveProject(projectDto.getId(), user1);
        SourceDto sourceDto2 = super.createSource(project2.getId());
        Entity entity2 = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto2.getId(), project2.getId(), 10, provenance, EntityStatus.UNMAPPED));

        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "HIGH"));
        when(zoomaService.annotate(eq(entity1.getName()), any(), any())).thenReturn(zoomaResponseDtos);
        when(zoomaService.annotate(eq(entity2.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(ArgumentMatchers.eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri1, "Orphanet:15", "Achondroplasia", "efo", false, true)})
        );

        matchmakerService.runMatchmaking(sourceDto1.getId(), project1);
        matchmakerService.runMatchmaking(sourceDto2.getId(), project2);

        Entity updated = entityService.retrieveEntity(entity1.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        updated = entityService.retrieveEntity(entity2.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(1, ontologyTerms.size());
        OntologyTerm ontologyTerm = ontologyTerms.get(0);
        List<OntologyTermContext> ontologyTermContexts = ontologyTermContextRepository.findByOntologyTermId(ontologyTerm.getId());
        assertEquals(2, ontologyTermContexts.size());

        Map<String, String> projectMap = new HashMap<>();
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            projectMap.put(ontologyTermContext.getProjectId(), "");
        }

        assertEquals(2, projectMap.size());
        assertTrue(projectMap.containsKey(project1.getId()));
        assertTrue(projectMap.containsKey(project2.getId()));
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            if (ontologyTermContext.getProjectId().equalsIgnoreCase(project1.getId())) {
                assertEquals(CurationConstants.CONTEXT_DEFAULT, ontologyTermContext.getContext());
                assertEquals(TermStatus.NEEDS_IMPORT.name(), ontologyTermContext.getStatus());
            }
            if (ontologyTermContext.getProjectId().equalsIgnoreCase(project2.getId())) {
                assertEquals(CurationConstants.CONTEXT_DEFAULT, ontologyTermContext.getContext());
                assertEquals(TermStatus.NEEDS_IMPORT.name(), ontologyTermContext.getStatus());
            }
        }
    }

    /**
     * Test case:
     * - Match 1 entity from 1 project in 2 different contexts annotated with the same term.
     * -- Expected: term should have 2 contexts
     */
    @Test
    public void testCase2() throws Exception {
        ProjectDto projectDto = super.createProject("New Project 1", user1, datasources, ontologies, "efo", 0, null);
        user1 = userService.findByEmail(user1.getEmail());

        Project project = projectService.retrieveProject(projectDto.getId(), user1);
        SourceDto sourceDto1 = super.createSource(project.getId());
        SourceDto sourceDto2 = super.createSource(project.getId());
        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());
        projectService.createProjectContext(new ProjectContext(null, "SECOND", project.getId(), "Description",
                datasources, ontologies, Arrays.asList(new String[]{"Orphanet"}), Arrays.asList(new String[]{"orphanet"}), null), project.getId(), user1);
        project = projectService.retrieveProject(projectDto.getId(), user1);

        Entity entity1 = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto1.getId(), project.getId(), 10, provenance, EntityStatus.UNMAPPED));
        Entity entity2 = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                "SECOND", sourceDto2.getId(), project.getId(), 10, provenance, EntityStatus.UNMAPPED));

        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "HIGH"));
        when(zoomaService.annotate(eq(entity1.getName()), any(), any())).thenReturn(zoomaResponseDtos);
        when(zoomaService.annotate(eq(entity2.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri1, "Orphanet:15", "Achondroplasia", "efo", false, true)})
        );

        matchmakerService.runMatchmaking(sourceDto1.getId(), project);
        matchmakerService.runMatchmaking(sourceDto2.getId(), project);

        Entity updated = entityService.retrieveEntity(entity1.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        updated = entityService.retrieveEntity(entity2.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(1, ontologyTerms.size());
        OntologyTerm ontologyTerm = ontologyTerms.get(0);
        List<OntologyTermContext> ontologyTermContexts = ontologyTermContextRepository.findByOntologyTermId(ontologyTerm.getId());
        assertEquals(2, ontologyTermContexts.size());

        Map<String, String> projectMap = new HashMap<>();
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            projectMap.put(ontologyTermContext.getProjectId(), "");
        }

        assertEquals(1, projectMap.size());
        assertTrue(projectMap.containsKey(project.getId()));
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            if (ontologyTermContext.getProjectId().equalsIgnoreCase(project.getId()) && ontologyTermContext.getContext().equals(CurationConstants.CONTEXT_DEFAULT)) {
                assertEquals(TermStatus.NEEDS_IMPORT.name(), ontologyTermContext.getStatus());
            }
            if (ontologyTermContext.getProjectId().equalsIgnoreCase(project.getId()) && ontologyTermContext.getContext().equals("SECOND")) {
                assertEquals(TermStatus.CURRENT.name(), ontologyTermContext.getStatus());
            }
        }
    }
}

package uk.ac.ebi.spot.ontostring;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.Provenance;
import uk.ac.ebi.spot.ontostring.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectContextGraphRestrictionDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontostring.service.*;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.constants.MappingStatus;
import uk.ac.ebi.spot.ontostring.constants.TermStatus;
import uk.ac.ebi.spot.ontostring.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontostring.service.*;

import java.util.*;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class MatchMakingWithGraphRestrictionTest extends IntegrationTest {

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
    private MappingService mappingService;

    @Autowired
    private OntologyTermService ontologyTermService;

    private List<String> datasources;

    private List<String> ontologies;

    @Override
    public void setup() throws Exception {
        super.setup();
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OXO", Arrays.asList(new String[]{"ordo::orphanet"})));

        datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
    }

    /**
     * Ancestor: EFO:0005541
     */
    @Test
    public void runMatchmakingWithAncestorTest() throws Exception {
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0,
                new ProjectContextGraphRestrictionDto(Arrays.asList(new String[]{"EFO:0005541"}),
                        Arrays.asList(new String[]{"http://www.ebi.ac.uk/efo/EFO_0005541"}),
                        Arrays.asList(new String[]{"rdfs:subClassOf"}),
                        false,
                        false));
        this.runMatchmaking(projectDto);
    }

    /**
     * Parent: EFO:0005802
     */
    @Test
    public void runMatchmakingWithParentTest() throws Exception {
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0,
                new ProjectContextGraphRestrictionDto(Arrays.asList(new String[]{"EFO:0005802"}),
                        Arrays.asList(new String[]{"http://www.ebi.ac.uk/efo/EFO_0005802"}),
                        Arrays.asList(new String[]{"rdfs:subClassOf"}),
                        true,
                        false));
        this.runMatchmaking(projectDto);
    }

    @Test
    public void runMatchmakingWithRandomAncestorTest() throws Exception {
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0,
                new ProjectContextGraphRestrictionDto(Arrays.asList(new String[]{"EFO:0105802"}),
                        Arrays.asList(new String[]{"http://www.ebi.ac.uk/efo/EFO_0005802"}),
                        Arrays.asList(new String[]{"rdfs:subClassOf"}),
                        false,
                        false));

        user1 = userService.findByEmail(user1.getEmail());
        Project project = projectService.retrieveProject(projectDto.getId(), user1);
        SourceDto sourceDto = super.createSource(project.getId());
        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());

        Entity entity = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), 10, provenance, EntityStatus.UNMAPPED));

        matchmakerService.runMatchmaking(sourceDto.getId(), project);
        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.UNMAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertTrue(ontologyTerms.isEmpty());
    }

    private void runMatchmaking(ProjectDto projectDto) throws Exception {
        user1 = userService.findByEmail(user1.getEmail());
        Project project = projectService.retrieveProject(projectDto.getId(), user1);
        SourceDto sourceDto = super.createSource(project.getId());
        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());

        Entity entity = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), 10, provenance, EntityStatus.UNMAPPED));

        matchmakerService.runMatchmaking(sourceDto.getId(), project);
        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getId(), ontologyTerm);
            if (ontologyTerm.getCurie().equalsIgnoreCase("Orphanet:15")) {
                Optional<OntologyTermContext> ontologyTermContextOp = ontologyTermContextRepository.findByOntologyTermIdAndProjectIdAndContext(ontologyTerm.getId(), project.getId(), updated.getContext());
                assertTrue(ontologyTermContextOp.isPresent());
                assertEquals(TermStatus.CURRENT.name(), ontologyTermContextOp.get().getStatus());
            }
        }

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNotNull(mapping);
        assertTrue(ontoMap.containsKey(mapping.getOntologyTermIds().get(0)));
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), mapping.getStatus());
    }
}

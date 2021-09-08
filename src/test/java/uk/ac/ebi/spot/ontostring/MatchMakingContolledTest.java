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
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontostring.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontostring.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontostring.service.*;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;
import uk.ac.ebi.spot.ontostring.constants.EntityStatus;
import uk.ac.ebi.spot.ontostring.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontostring.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontostring.service.*;

import java.util.*;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class,
        IntegrationTest.MockZoomaServiceConfig.class,
        IntegrationTest.MockOLSServiceConfig.class})
public class MatchMakingContolledTest extends IntegrationTest {

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
    private MappingSuggestionsService mappingSuggestionsService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private OntologyTermService ontologyTermService;

    private Project project;

    private SourceDto sourceDto;

    private Entity entity;

    private List<String> datasources;

    private List<String> ontologies;

    @Autowired
    private ZoomaService zoomaService;

    @Autowired
    private OLSService olsService;

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Override
    public void setup() throws Exception {
        super.setup();
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OXO", Arrays.asList(new String[]{"ordo::orphanet"})));

        this.datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        this.ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});

        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0, null);
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
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), 10, provenance, EntityStatus.UNMAPPED));
    }


    /**
     * Test case:
     * - Get from Zooma at least 1 high confidence response; Ontology matches preferred. OLS returns the same IRI.
     * -- Expect: mapping to be created. Number of mapping suggestions = number of ontology terms 'created'
     */
    @Test
    public void testCase1() {
        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";
        String iri2 = "http://purl.obolibrary.org/obo/MONDO_0007037";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "HIGH"));
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri2}), "GOOD"));

        when(zoomaService.annotate(eq(entity.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(ArgumentMatchers.eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri1, "Orphanet:15", "Achondroplasia", "efo", false, true)})
        );
        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri2)), eq(iri2))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri2, "MONDO:0007037", "achondroplasia", "efo", false, true)})
        );

        matchmakerService.runMatchmaking(sourceDto.getId(), project);

        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(2, ontologyTerms.size());
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getCurie(), ontologyTerm);
        }
        assertTrue(ontoMap.containsKey("Orphanet:15"));
        assertTrue(ontoMap.containsKey("MONDO:0007037"));

        Map<String, List<MappingSuggestion>> mappingSuggestionMap = mappingSuggestionsService.retrieveMappingSuggestionsForEntities(Arrays.asList(new String[]{entity.getId()}), entity.getProjectId(), entity.getContext());
        assertEquals(1, mappingSuggestionMap.size());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionMap.get(entity.getId());
        assertEquals(2, mappingSuggestions.size());

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNotNull(mapping);
    }

    /**
     * Test case:
     * - Get from Zooma at least 1 high confidence response; Ontology matches preferred. OLS returns a different IRI - not in high confidence.
     * -- Expect: No mapping created. Number of mapping suggestions = number of ontology terms 'created'
     */
    @Test
    public void testCase2() {
        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";
        String iri2 = "http://purl.obolibrary.org/obo/MONDO_0007037";
        String iri3 = "http://purl.obolibrary.org/obo/ICD_12356";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "HIGH"));
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri2}), "GOOD"));

        when(zoomaService.annotate(eq(entity.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri3, "ICD:12356", "Pseudo-achondroplasia", "efo", false, true)})
        );
        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri2)), eq(iri2))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri2, "MONDO:0007037", "ACH", "efo", false, true)})
        );

        matchmakerService.runMatchmaking(sourceDto.getId(), project);

        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.SUGGESTIONS_PROVIDED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(2, ontologyTerms.size());
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getCurie(), ontologyTerm);
        }
        assertTrue(ontoMap.containsKey("ICD:12356"));
        assertTrue(ontoMap.containsKey("MONDO:0007037"));

        Map<String, List<MappingSuggestion>> mappingSuggestionMap = mappingSuggestionsService.retrieveMappingSuggestionsForEntities(Arrays.asList(new String[]{entity.getId()}), entity.getProjectId(), entity.getContext());
        assertEquals(1, mappingSuggestionMap.size());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionMap.get(entity.getId());
        assertEquals(2, mappingSuggestions.size());

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNull(mapping);
    }

    /**
     * Test case:
     * - Get from Zooma at least 1 high confidence response; Ontology does not match preferred.
     * -- Expect: No mapping created. Number of mapping suggestions = number of ontology terms 'created'
     */
    @Test
    public void testCase3() {
        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";
        String iri2 = "http://purl.obolibrary.org/obo/MONDO_0007037";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "HIGH"));
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri2}), "GOOD"));

        when(zoomaService.annotate(eq(entity.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri1, "Orphanet:15", "Achondroplasia", "efo", false, true)})
        );
        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri2)), eq(iri2))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri2, "MONDO:0007037", "ACH", "efo", false, true)})
        );

        this.ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp"});
        ProjectContext projectContext = project.getContexts().get(0);
        projectContext.setOntologies(this.ontologies);
        projectService.updateProjectContext(projectContext, project.getId(), user1);
        project = projectService.retrieveProject(project.getId(), user1);

        matchmakerService.runMatchmaking(sourceDto.getId(), project);

        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.SUGGESTIONS_PROVIDED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(1, ontologyTerms.size());
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getCurie(), ontologyTerm);
        }
        assertEquals("MONDO:0007037", ontologyTerms.get(0).getCurie());

        Map<String, List<MappingSuggestion>> mappingSuggestionMap = mappingSuggestionsService.retrieveMappingSuggestionsForEntities(Arrays.asList(new String[]{entity.getId()}), entity.getProjectId(), entity.getContext());
        assertEquals(1, mappingSuggestionMap.size());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionMap.get(entity.getId());
        assertEquals(1, mappingSuggestions.size());

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNull(mapping);
    }

    /**
     * Test case:
     * - Get from Zooma no high confidence responses; Label matches
     * -- Expect: mapping to be created. Number of mapping suggestions = number of ontology terms 'created'
     */
    @Test
    public void testCase4() {
        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";
        String iri2 = "http://purl.obolibrary.org/obo/MONDO_0007037";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "GOOD"));
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri2}), "GOOD"));

        when(zoomaService.annotate(eq(entity.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri1, "Orphanet:15", "Achondroplasia", "efo", false, true)})
        );
        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri2)), eq(iri2))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri2, "MONDO:0007037", "ACH", "efo", false, true)})
        );

        matchmakerService.runMatchmaking(sourceDto.getId(), project);

        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(2, ontologyTerms.size());
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getCurie(), ontologyTerm);
        }
        assertTrue(ontoMap.containsKey("Orphanet:15"));
        assertTrue(ontoMap.containsKey("MONDO:0007037"));

        Map<String, List<MappingSuggestion>> mappingSuggestionMap = mappingSuggestionsService.retrieveMappingSuggestionsForEntities(Arrays.asList(new String[]{entity.getId()}), entity.getProjectId(), entity.getContext());
        assertEquals(1, mappingSuggestionMap.size());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionMap.get(entity.getId());
        assertEquals(2, mappingSuggestions.size());

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNotNull(mapping);
    }

    /**
     * Test case:
     * - Get from Zooma no high confidence responses; Label does not match
     * -- Expect: No mapping created. Number of mapping suggestions = number of ontology terms 'created'
     */
    @Test
    public void testCase5() {
        String iri1 = "http://www.orpha.net/ORDO/Orphanet_15";
        String iri2 = "http://purl.obolibrary.org/obo/MONDO_0007037";

        List<ZoomaResponseDto> zoomaResponseDtos = new ArrayList<>();
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri1}), "GOOD"));
        zoomaResponseDtos.add(new ZoomaResponseDto(Arrays.asList(new String[]{iri2}), "GOOD"));

        when(zoomaService.annotate(eq(entity.getName()), any(), any())).thenReturn(zoomaResponseDtos);

        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri1)), eq(iri1))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri1, "Orphanet:15", "Pseudo-achondroplasia", "efo", false, true)})
        );
        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(iri2)), eq(iri2))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(iri2, "MONDO:0007037", "ACH", "efo", false, true)})
        );

        matchmakerService.runMatchmaking(sourceDto.getId(), project);

        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.SUGGESTIONS_PROVIDED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findAll();
        assertEquals(2, ontologyTerms.size());
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getCurie(), ontologyTerm);
        }
        assertTrue(ontoMap.containsKey("Orphanet:15"));
        assertTrue(ontoMap.containsKey("MONDO:0007037"));

        Map<String, List<MappingSuggestion>> mappingSuggestionMap = mappingSuggestionsService.retrieveMappingSuggestionsForEntities(Arrays.asList(new String[]{entity.getId()}), entity.getProjectId(), entity.getContext());
        assertEquals(1, mappingSuggestionMap.size());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionMap.get(entity.getId());
        assertEquals(2, mappingSuggestions.size());

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNull(mapping);
    }
}

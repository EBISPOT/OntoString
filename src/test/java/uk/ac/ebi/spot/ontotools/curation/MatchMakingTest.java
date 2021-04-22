package uk.ac.ebi.spot.ontotools.curation;

import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class})
public class MatchMakingTest extends IntegrationTest {

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

    @Override
    public void setup() throws Exception {
        super.setup();
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OXO", Arrays.asList(new String[]{"ordo::orphanet"})));

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});

        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0);
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

    @Test
    public void runMatchmakingTest() {
        matchmakerService.runMatchmaking(sourceDto.getId(), project);
        Entity updated = entityService.retrieveEntity(entity.getId());
        assertEquals(EntityStatus.AUTO_MAPPED, updated.getMappingStatus());

        List<OntologyTerm> ontologyTerms = ontologyTermService.retrieveAllTerms();
        Map<String, OntologyTerm> ontoMap = new HashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontoMap.put(ontologyTerm.getId(), ontologyTerm);
            if (ontologyTerm.getCurie().equalsIgnoreCase("Orphanet:15")) {
                assertEquals(TermStatus.CURRENT.name(), CurationUtil.termStatusForContext(ontologyTerm, project.getId(), updated.getContext()));
            }
            if (ontologyTerm.getCurie().equalsIgnoreCase("MONDO:0007037")) {
                assertEquals(TermStatus.NEEDS_IMPORT.name(), CurationUtil.termStatusForContext(ontologyTerm, project.getId(), updated.getContext()));
            }
        }

        Map<String, List<MappingSuggestion>> mappingSuggestionMap = mappingSuggestionsService.retrieveMappingSuggestionsForEntities(Arrays.asList(new String[]{entity.getId()}));
        assertEquals(1, mappingSuggestionMap.size());
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionMap.get(entity.getId());
        assertEquals(2, mappingSuggestions.size());

        for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
            assertTrue(ontoMap.containsKey(mappingSuggestion.getOntologyTermId()));
        }

        Mapping mapping = mappingService.retrieveMappingForEntity(entity.getId());
        assertNotNull(mapping);
        assertTrue(ontoMap.containsKey(mapping.getOntologyTermIds().get(0)));
        assertEquals(MappingStatus.AWAITING_REVIEW.name(), mapping.getStatus());
    }
}

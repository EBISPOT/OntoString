package uk.ac.ebi.spot.ontotools.curation.tasks;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import uk.ac.ebi.spot.ontotools.curation.IntegrationTest;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontotools.curation.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermUpdateLogEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.EntityService;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.service.ProjectService;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.when;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class,
        IntegrationTest.MockOLSServiceConfig.class})
public class OntologyTermUpdateTaskTest extends IntegrationTest {

    @Autowired
    private ExternalServiceConfigRepository externalServiceConfigRepository;

    @Autowired
    private OntologyTermUpdateTask ontologyTermUpdateTask;

    @Autowired
    private OLSService olsService;

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private OntologyTermUpdateLogEntryRepository ontologyTermUpdateLogEntryRepository;

    @Autowired
    private EntityService entityService;

    @Autowired
    private ProjectService projectService;

    private OntologyTerm orphaTerm;

    private OntologyTerm mondoTerm;

    private Mapping mapping;

    @Override
    public void setup() throws Exception {
        super.setup();

        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OLS", Arrays.asList(new String[]{"orphanet::ordo"})));
        externalServiceConfigRepository.insert(new ExternalServiceConfig(null, "OXO", Arrays.asList(new String[]{"ordo::orphanet"})));

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});

        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "mondo", 0);
        user1 = userService.findByEmail(user1.getEmail());
        Project project = projectService.retrieveProject(projectDto.getId(), user1);
        SourceDto sourceDto = super.createSource(project.getId());
        Provenance provenance = new Provenance(user1.getName(), user1.getEmail(), DateTime.now());

        this.orphaTerm = ontologyTermRepository.insert(new OntologyTerm(null, "Orphanet:15", "http://www.orpha.net/ORDO/Orphanet_15",
                DigestUtils.sha256Hex("http://www.orpha.net/ORDO/Orphanet_15"), "Achondroplasia",
                Arrays.asList(new OntologyTermContext[]{
                        new OntologyTermContext(project.getId(), CurationConstants.CONTEXT_DEFAULT, TermStatus.CURRENT.name())
                }), null, null));

        this.mondoTerm = ontologyTermRepository.insert(new OntologyTerm(null, "MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037",
                DigestUtils.sha256Hex("http://purl.obolibrary.org/obo/MONDO_0007037"), "Achondroplasia", Arrays.asList(new OntologyTermContext[]{
                new OntologyTermContext(project.getId(), CurationConstants.CONTEXT_DEFAULT, TermStatus.NEEDS_IMPORT.name())
        }), null, null));

        entity = entityService.createEntity(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, sourceDto.getId(), project.getId(), 10, provenance, EntityStatus.UNMAPPED));

        mapping = mappingRepository.insert(new Mapping(null, entity.getId(), entity.getContext(), Arrays.asList(new String[]{orphaTerm.getId()}),
                entity.getProjectId(), false, new ArrayList<>(), new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(),
                new Provenance(user1.getName(), user1.getEmail(), DateTime.now()), null));

        when(olsService.retrieveTerms(eq(CurationUtil.ontoFromIRI(orphaTerm.getIri())), eq(orphaTerm.getIri()))).thenReturn(new ArrayList<>());
        when(olsService.retrieveTerms(eq("mondo"), eq(mondoTerm.getIri()))).thenReturn(
                Arrays.asList(new OLSTermDto[]{new OLSTermDto(mondoTerm.getIri(), "MONDO:0007037", "Achondroplasia", false, true)})
        );
    }

    @Test
    public void shouldRunUpdate() {
        ontologyTermUpdateTask.updateOntologyTerms();

        OntologyTerm ontologyTerm = ontologyTermRepository.findById(this.orphaTerm.getId()).get();
        assertEquals(TermStatus.DELETED.name(), ontologyTerm.getContexts().get(0).getStatus());

        Mapping newMapping = mappingRepository.findById(mapping.getId()).get();
        assertEquals(MappingStatus.HAS_OBSOLETE_TERM.name(), newMapping.getStatus());

        ontologyTerm = ontologyTermRepository.findById(this.mondoTerm.getId()).get();
        assertEquals(TermStatus.CURRENT.name(), ontologyTerm.getContexts().get(0).getStatus());
        assertEquals(2, ontologyTermUpdateLogEntryRepository.findAll().size());
    }
}
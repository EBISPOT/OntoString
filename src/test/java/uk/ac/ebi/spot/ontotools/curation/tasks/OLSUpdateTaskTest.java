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
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.repository.EntityRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermUpdateLogEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;

import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@ContextConfiguration(classes = {IntegrationTest.MockTaskExecutorConfig.class,
        IntegrationTest.MockOLSServiceConfig.class})
public class OLSUpdateTaskTest extends IntegrationTest {

    @Autowired
    private OLSUpdateTask olsUpdateTask;

    @Autowired
    private OLSService olsService;

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private OntologyTermUpdateLogEntryRepository ontologyTermUpdateLogEntryRepository;

    @Autowired
    private EntityRepository entityRepository;

    private OntologyTerm orphaTerm;

    private OntologyTerm mondoTerm;

    private Mapping mapping;

    @Override
    public void setup() throws Exception {
        super.setup();
        this.orphaTerm = ontologyTermRepository.insert(new OntologyTerm(null, "Orphanet:15", "http://www.orpha.net/ORDO/Orphanet_15",
                DigestUtils.sha256Hex("http://www.orpha.net/ORDO/Orphanet_15"), "Achondroplasia", TermStatus.CURRENT.name(), null, null));
        this.mondoTerm = ontologyTermRepository.insert(new OntologyTerm(null, "MONDO:0007037", "http://purl.obolibrary.org/obo/MONDO_0007037",
                DigestUtils.sha256Hex("http://purl.obolibrary.org/obo/MONDO_0007037"), "Achondroplasia", TermStatus.NEEDS_IMPORT.name(), null, null));

        Entity entity = entityRepository.insert(new Entity(null, "Achondroplasia", RandomStringUtils.randomAlphabetic(10),
                CurationConstants.CONTEXT_DEFAULT, RandomStringUtils.randomAlphabetic(10), RandomStringUtils.randomAlphabetic(10),
                null, new Provenance(user1.getName(), user1.getEmail(), DateTime.now()), EntityStatus.AUTO_MAPPED));

        mapping = mappingRepository.insert(new Mapping(null, entity.getId(), Arrays.asList(new String[]{orphaTerm.getId()}),
                entity.getProjectId(), false, new ArrayList<>(), new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(),
                new Provenance(user1.getName(), user1.getEmail(), DateTime.now()), null));

        when(olsService.retrieveOriginalTerm(this.mondoTerm.getIri())).thenReturn(null);
        when(olsService.retrieveOriginalTerm(this.orphaTerm.getIri())).thenReturn(new OLSTermDto(this.orphaTerm.getIri(),
                this.orphaTerm.getCurie(), this.orphaTerm.getLabel(), true, true));
    }

    @Test
    public void shouldRunUpdate() {
        olsUpdateTask.updateOntologyTerms();

        OntologyTerm ontologyTerm = ontologyTermRepository.findById(this.orphaTerm.getId()).get();
        assertEquals(TermStatus.OBSOLETE.name(), ontologyTerm.getStatus());

        ontologyTerm = ontologyTermRepository.findById(this.mondoTerm.getId()).get();
        assertEquals(TermStatus.DELETED.name(), ontologyTerm.getStatus());
        assertEquals(2, ontologyTermUpdateLogEntryRepository.findAll().size());

        Mapping newMapping = mappingRepository.findById(mapping.getId()).get();
        assertEquals(MappingStatus.HAS_OBSOLETE_TERM.name(), newMapping.getStatus());

        verify(olsService, times(1)).retrieveOriginalTerm(eq(this.mondoTerm.getIri()));
        verify(olsService, times(1)).retrieveOriginalTerm(eq(this.orphaTerm.getIri()));
    }
}
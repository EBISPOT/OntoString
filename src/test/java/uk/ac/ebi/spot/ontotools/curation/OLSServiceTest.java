package uk.ac.ebi.spot.ontotools.curation;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

public class OLSServiceTest extends IntegrationTest {

    @Autowired
    private OLSService olsService;

    @Test
    public void shouldRetrieveTerms() {
        String ontoId = "ordo";
        String termId = "http://www.orpha.net/ORDO/Orphanet_15";

        List<OLSTermDto> terms = olsService.retrieveTerms(ontoId, termId);
        assertEquals(1, terms.size());
        assertEquals(termId, terms.get(0).getIri());
        assertEquals("Achondroplasia", terms.get(0).getLabel());
        assertEquals("Orphanet:15", terms.get(0).getCurie());
        assertFalse(terms.get(0).getObsolete());
    }

    @Test
    public void shouldQuery() {
        String prefix = "diabetes";

        List<OLSQueryDocDto> docs = olsService.query(prefix);
        assertEquals(10, docs.size());

        List<String> efoUris = new ArrayList<>();
        for (OLSQueryDocDto olsQueryDocDto : docs) {
            if (olsQueryDocDto.getOntologyName().equalsIgnoreCase("efo")) {
                efoUris.add(olsQueryDocDto.getCurie());
            }
        }
        assertTrue(efoUris.contains("EFO:0000400"));
    }

    @Test
    public void shouldGetTerm() {
        String iri = "http://www.ebi.ac.uk/efo/EFO_0001444";

        OLSTermDto olsTermDto = olsService.retrieveOriginalTerm(iri);
        assertNotNull(olsTermDto);
    }

    @Test
    public void shouldRetrieveAncestors() {
        String ontoId = "efo";
        String termId = "http://www.orpha.net/ORDO/Orphanet_15";

        List<OLSTermDto> terms = olsService.retrieveAncestors(ontoId, termId, false);
        assertEquals(23, terms.size());
    }
}

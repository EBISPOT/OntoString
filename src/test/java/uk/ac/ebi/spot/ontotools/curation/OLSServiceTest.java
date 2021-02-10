package uk.ac.ebi.spot.ontotools.curation;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

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

}

package uk.ac.ebi.spot.ontotools.curation.rest.dto.ols;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OLSQueryResponseEntryDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OLSQueryResponseEntryDto.class)
                .verify();
    }
}
package uk.ac.ebi.spot.ontotools.curation.rest.dto.ols;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OLSPageDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OLSPageDto.class)
                .verify();
    }

}
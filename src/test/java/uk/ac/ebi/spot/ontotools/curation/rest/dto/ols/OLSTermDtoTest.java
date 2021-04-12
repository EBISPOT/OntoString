package uk.ac.ebi.spot.ontotools.curation.rest.dto.ols;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OLSTermDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OLSTermDto.class)
                .verify();
    }


}
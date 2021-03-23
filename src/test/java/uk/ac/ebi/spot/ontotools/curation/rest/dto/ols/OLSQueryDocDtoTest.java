package uk.ac.ebi.spot.ontotools.curation.rest.dto.ols;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OLSQueryDocDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OLSQueryDocDto.class)
                .verify();
    }
}
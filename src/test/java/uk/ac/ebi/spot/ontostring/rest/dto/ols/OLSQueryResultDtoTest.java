package uk.ac.ebi.spot.ontostring.rest.dto.ols;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OLSQueryResultDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OLSQueryResultDto.class)
                .verify();
    }

}
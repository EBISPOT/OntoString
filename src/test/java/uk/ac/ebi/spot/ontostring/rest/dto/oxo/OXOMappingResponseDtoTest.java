package uk.ac.ebi.spot.ontostring.rest.dto.oxo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OXOMappingResponseDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OXOMappingResponseDto.class)
                .verify();
    }

}
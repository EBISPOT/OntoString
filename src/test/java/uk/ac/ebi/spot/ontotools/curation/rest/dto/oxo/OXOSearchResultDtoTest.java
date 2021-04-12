package uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OXOSearchResultDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OXOSearchResultDto.class)
                .verify();
    }

}
package uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OXOEmbeddedDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OXOEmbeddedDto.class)
                .verify();
    }

}
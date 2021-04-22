package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ShortEntityInfoDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ShortEntityInfoDto.class)
                .verify();
    }

}
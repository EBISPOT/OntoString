package uk.ac.ebi.spot.ontotools.curation.rest.dto.config;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ExternalServiceConfigDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ExternalServiceConfigDto.class)
                .verify();
    }

}
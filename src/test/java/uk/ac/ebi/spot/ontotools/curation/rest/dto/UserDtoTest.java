package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class UserDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserDto.class)
                .verify();
    }
}
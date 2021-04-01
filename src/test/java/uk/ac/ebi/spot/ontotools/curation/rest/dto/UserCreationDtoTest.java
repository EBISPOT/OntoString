package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class UserCreationDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserCreationDto.class)
                .verify();
    }

}
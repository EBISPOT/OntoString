package uk.ac.ebi.spot.ontostring.rest.dto.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class UserCreationDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserCreationDto.class)
                .verify();
    }

}
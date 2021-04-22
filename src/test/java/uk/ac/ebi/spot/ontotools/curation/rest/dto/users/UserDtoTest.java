package uk.ac.ebi.spot.ontotools.curation.rest.dto.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.UserDto;

public class UserDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserDto.class)
                .verify();
    }
}
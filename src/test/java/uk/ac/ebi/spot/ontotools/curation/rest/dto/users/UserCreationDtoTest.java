package uk.ac.ebi.spot.ontotools.curation.rest.dto.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.UserCreationDto;

public class UserCreationDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(UserCreationDto.class)
                .verify();
    }

}
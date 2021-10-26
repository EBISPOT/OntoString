package uk.ac.ebi.spot.ontostring.rest.dto.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class RoleDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(RoleDto.class)
                .verify();
    }

}
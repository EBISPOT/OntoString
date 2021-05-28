package uk.ac.ebi.spot.ontotools.curation.rest.dto.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.RoleDto;

public class RoleDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(RoleDto.class)
                .verify();
    }

}
package uk.ac.ebi.spot.ontotools.curation.rest.dto.users;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.ProjectUserDto;

public class ProjectUserDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectUserDto.class)
                .verify();
    }
}
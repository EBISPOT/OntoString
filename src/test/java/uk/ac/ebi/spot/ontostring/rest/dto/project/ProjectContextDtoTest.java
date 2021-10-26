package uk.ac.ebi.spot.ontostring.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ProjectContextDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectContextDto.class)
                .verify();
    }

}
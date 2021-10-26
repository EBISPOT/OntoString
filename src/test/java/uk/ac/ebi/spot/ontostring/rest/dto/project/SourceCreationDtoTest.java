package uk.ac.ebi.spot.ontostring.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class SourceCreationDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SourceCreationDto.class)
                .verify();
    }

}
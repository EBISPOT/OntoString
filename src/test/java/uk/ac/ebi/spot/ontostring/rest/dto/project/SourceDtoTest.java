package uk.ac.ebi.spot.ontostring.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class SourceDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SourceDto.class)
                .verify();
    }

}
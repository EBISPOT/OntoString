package uk.ac.ebi.spot.ontostring.rest.dto.export;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ExportEntityDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ExportEntityDto.class)
                .verify();
    }

}
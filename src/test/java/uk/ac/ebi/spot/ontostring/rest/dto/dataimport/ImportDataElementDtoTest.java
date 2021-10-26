package uk.ac.ebi.spot.ontostring.rest.dto.dataimport;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ImportDataElementDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ImportDataElementDto.class)
                .verify();
    }

}
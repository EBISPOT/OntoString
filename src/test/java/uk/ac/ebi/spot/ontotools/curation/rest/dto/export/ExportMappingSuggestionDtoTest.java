package uk.ac.ebi.spot.ontotools.curation.rest.dto.export;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ExportMappingSuggestionDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ExportMappingSuggestionDto.class)
                .verify();
    }


}
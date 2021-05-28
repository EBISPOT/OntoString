package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.SourceDto;

public class SourceDtoTest {
    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SourceDto.class)
                .verify();
    }

}
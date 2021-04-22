package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.SourceCreationDto;

public class SourceCreationDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(SourceCreationDto.class)
                .verify();
    }

}
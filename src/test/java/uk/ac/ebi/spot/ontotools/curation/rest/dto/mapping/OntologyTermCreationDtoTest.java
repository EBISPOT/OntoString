package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class OntologyTermCreationDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(OntologyTermCreationDto.class)
                .verify();
    }

}
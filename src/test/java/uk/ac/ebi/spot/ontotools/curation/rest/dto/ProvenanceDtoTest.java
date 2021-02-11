package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ProvenanceDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProvenanceDto.class)
                .verify();
    }

}
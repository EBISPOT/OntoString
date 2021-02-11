package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ReviewDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ReviewDto.class)
                .verify();
    }

}
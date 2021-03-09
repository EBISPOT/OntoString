package uk.ac.ebi.spot.ontotools.curation.rest.dto.audit;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class AuditUserDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(AuditUserDto.class)
                .verify();
    }

}
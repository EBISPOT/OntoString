package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;

public class ProjectExportStatusDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectExportStatusDto.class)
                .verify();
    }

}
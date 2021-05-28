package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectExportStatusDto;

public class ProjectExportStatusDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectExportStatusDto.class)
                .verify();
    }

}
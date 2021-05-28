package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectContextDto;

public class ProjectContextDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectContextDto.class)
                .verify();
    }

}
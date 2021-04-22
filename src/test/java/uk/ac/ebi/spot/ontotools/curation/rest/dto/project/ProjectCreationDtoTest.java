package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectCreationDto;

public class ProjectCreationDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectCreationDto.class)
                .verify();
    }

}
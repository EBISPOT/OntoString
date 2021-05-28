package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.Test;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectDto;

public class ProjectDtoTest {

    @Test
    public void equalsContract() {
        EqualsVerifier.forClass(ProjectDto.class)
                .verify();
    }

}
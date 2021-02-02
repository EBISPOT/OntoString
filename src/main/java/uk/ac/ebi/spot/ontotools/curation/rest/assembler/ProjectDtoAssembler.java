package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;

public class ProjectDtoAssembler {

    public static ProjectDto assemble(Project project) {
        return new ProjectDto(project.getId(),
                project.getName(),
                project.getDescription());
    }
}

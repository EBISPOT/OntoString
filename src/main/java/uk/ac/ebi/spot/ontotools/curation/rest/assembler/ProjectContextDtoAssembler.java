package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectContextDto;

import java.util.ArrayList;
import java.util.List;

public class ProjectContextDtoAssembler {

    public static ProjectContextDto assemble(ProjectContext projectContext) {
        return new ProjectContextDto(projectContext.getName(),
                projectContext.getDescription(),
                projectContext.getDatasources(),
                projectContext.getOntologies(),
                projectContext.getPreferredMappingOntologies());
    }

    public static ProjectContext disassemble(ProjectContextDto projectContext) {
        List<String> preferredMappingLower = new ArrayList<>();
        if (projectContext.getPreferredMappingOntologies() != null) {
            for (String entry : projectContext.getPreferredMappingOntologies()) {
                preferredMappingLower.add(entry.toLowerCase());
            }
        }

        return new ProjectContext(null,
                projectContext.getName(),
                null,
                projectContext.getDescription(),
                projectContext.getDatasources(),
                projectContext.getOntologies(),
                projectContext.getPreferredMappingOntologies(),
                preferredMappingLower);
    }
}

package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.ProjectContext;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectContextDto;

import java.util.ArrayList;
import java.util.List;

public class ProjectContextDtoAssembler {

    public static ProjectContextDto assemble(ProjectContext projectContext) {
        return new ProjectContextDto(projectContext.getName(),
                projectContext.getDescription(),
                projectContext.getDatasources(),
                projectContext.getOntologies(),
                projectContext.getPreferredMappingOntologies(),
                projectContext.getProjectContextGraphRestriction() != null ? ProjectContextGraphRestrictionDtoAssembler.assemble(projectContext.getProjectContextGraphRestriction()) : null);
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
                preferredMappingLower,
                projectContext.getGraphRestriction() != null ? ProjectContextGraphRestrictionDtoAssembler.disassemble(projectContext.getGraphRestriction()) : null);
    }
}

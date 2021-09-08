package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.ProjectContextGraphRestriction;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectContextGraphRestrictionDto;

public class ProjectContextGraphRestrictionDtoAssembler {

    public static ProjectContextGraphRestrictionDto assemble(ProjectContextGraphRestriction projectContextGraphRestriction) {
        return new ProjectContextGraphRestrictionDto(projectContextGraphRestriction.getClasses(),
                projectContextGraphRestriction.getIris(),
                projectContextGraphRestriction.getRelations(),
                projectContextGraphRestriction.getDirect(),
                projectContextGraphRestriction.getIncludeSelf());
    }

    public static ProjectContextGraphRestriction disassemble(ProjectContextGraphRestrictionDto projectContextGraphRestrictionDto) {
        return new ProjectContextGraphRestriction(projectContextGraphRestrictionDto.getClasses(),
                projectContextGraphRestrictionDto.getIris(),
                projectContextGraphRestrictionDto.getRelations(),
                projectContextGraphRestrictionDto.getDirect(),
                projectContextGraphRestrictionDto.getIncludeSelf());
    }
}

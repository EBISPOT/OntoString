package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContextGraphRestriction;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.project.ProjectContextGraphRestrictionDto;

public class ProjectContextGraphRestrictionDtoAssembler {

    public static ProjectContextGraphRestrictionDto assemble(ProjectContextGraphRestriction projectContextGraphRestriction) {
        return new ProjectContextGraphRestrictionDto(projectContextGraphRestriction.getClasses(),
                projectContextGraphRestriction.getRelations(),
                projectContextGraphRestriction.getDirect(),
                projectContextGraphRestriction.getIncludeSelf());
    }

    public static ProjectContextGraphRestriction disassemble(ProjectContextGraphRestrictionDto projectContextGraphRestrictionDto) {
        return new ProjectContextGraphRestriction(projectContextGraphRestrictionDto.getClasses(),
                projectContextGraphRestrictionDto.getRelations(),
                projectContextGraphRestrictionDto.getDirect(),
                projectContextGraphRestrictionDto.getIncludeSelf());
    }
}

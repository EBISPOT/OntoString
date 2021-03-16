package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import org.apache.commons.lang3.tuple.Pair;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class ProjectDtoAssembler {

    public static ProjectDto assemble(Project project) {
        return new ProjectDto(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getContexts().stream().map(ProjectContextDtoAssembler::assemble).collect(Collectors.toList()),
                project.getNumberOfReviewsRequired(),
                ProvenanceDtoAssembler.assemble(project.getCreated()));
    }

    public static Project disassemble(ProjectDto project) {
        return new Project(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getNumberOfReviewsRequired(),
                null,
                ProvenanceDtoAssembler.disassemble(project.getCreated()),
                null);
    }

    public static Pair<Project, ProjectContext> disassemble(ProjectCreationDto project, Provenance provenance) {
        return Pair.of(new Project(null,
                        project.getName(),
                        project.getDescription(),
                        project.getNumberOfReviewsRequired(),
                        new ArrayList<>(),
                        provenance, null),
                new ProjectContext(null, CurationConstants.CONTEXT_DEFAULT, null, "Default context",
                        project.getDatasources(), project.getOntologies(), project.getPreferredMappingOntologies()));
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectDto;

import java.util.ArrayList;

public class ProjectDtoAssembler {

    public static ProjectDto assemble(Project project) {
        return new ProjectDto(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDatasources(),
                project.getOntologies(),
                project.getPreferredMappingOntology(),
                ProvenanceDtoAssembler.assemble(project.getCreated()));
    }

    public static Project disassemble(ProjectDto project) {
        return new Project(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDatasources(),
                project.getOntologies(),
                project.getPreferredMappingOntology(),
                ProvenanceDtoAssembler.disassemble(project.getCreated()));
    }

    public static Project disassemble(ProjectCreationDto project, Provenance provenance) {
        return new Project(null,
                project.getName(),
                project.getDescription(),
                project.getDatasources() != null ? project.getDatasources() : new ArrayList<>(),
                project.getOntologies() != null ? project.getOntologies() : new ArrayList<>(),
                project.getPreferredMappingOntology(),
                provenance);
    }
}

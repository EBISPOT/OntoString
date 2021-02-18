package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Project;
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
                project.getDatasources() != null ? project.getDatasources().stream().map(ProjectMappingConfigDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>(),
                project.getOntologies() != null ? project.getOntologies().stream().map(ProjectMappingConfigDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>(),
                project.getPreferredMappingOntologies(),
                ProvenanceDtoAssembler.assemble(project.getCreated()));
    }

    public static Project disassemble(ProjectDto project) {
        return new Project(project.getId(),
                project.getName(),
                project.getDescription(),
                project.getDatasources() != null ? project.getDatasources().stream().map(ProjectMappingConfigDtoAssembler::disassemble).collect(Collectors.toList()) : new ArrayList<>(),
                project.getOntologies() != null ? project.getOntologies().stream().map(ProjectMappingConfigDtoAssembler::disassemble).collect(Collectors.toList()) : new ArrayList<>(),
                project.getPreferredMappingOntologies(),
                ProvenanceDtoAssembler.disassemble(project.getCreated()));
    }

    public static Project disassemble(ProjectCreationDto project, Provenance provenance) {
        return new Project(null,
                project.getName(),
                project.getDescription(),
                project.getDatasources() != null ? project.getDatasources().stream().map(ProjectMappingConfigDtoAssembler::disassemble).collect(Collectors.toList()) : new ArrayList<>(),
                project.getOntologies() != null ? project.getOntologies().stream().map(ProjectMappingConfigDtoAssembler::disassemble).collect(Collectors.toList()) : new ArrayList<>(),
                project.getPreferredMappingOntologies(),
                provenance);
    }
}

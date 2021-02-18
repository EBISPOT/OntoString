package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.config.ProjectMappingConfig;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProjectMappingConfigDto;

public class ProjectMappingConfigDtoAssembler {

    public static ProjectMappingConfigDto assemble(ProjectMappingConfig projectMappingConfig) {
        return new ProjectMappingConfigDto(projectMappingConfig.getField(), projectMappingConfig.getMappingList());
    }

    public static ProjectMappingConfig disassemble(ProjectMappingConfigDto projectMappingConfig) {
        return new ProjectMappingConfig(projectMappingConfig.getField(), projectMappingConfig.getMappingList());
    }
}

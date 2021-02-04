package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.Role;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.RoleDto;

public class RoleDtoAssembler {

    public static RoleDto assemble(Role role) {
        return new RoleDto(role.getProjectId(), role.getRole().name());
    }
}

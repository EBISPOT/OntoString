package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.auth.Role;
import uk.ac.ebi.spot.ontostring.constants.ProjectRole;
import uk.ac.ebi.spot.ontostring.rest.dto.users.RoleDto;

public class RoleDtoAssembler {

    public static RoleDto assemble(Role role) {
        return new RoleDto(role.getProjectId(), role.getRole().name());
    }

    public static Role disassemble(RoleDto role) {
        return new Role(role.getProjectId(), ProjectRole.valueOf(role.getRole()));
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.UserDto;

import java.util.stream.Collectors;

public class UserDtoAssembler {

    public static UserDto asseble(User user) {
        return new UserDto(user.getId(),
                user.getName(),
                user.getEmail(),
                user.getRoles() != null ? user.getRoles().stream().map(RoleDtoAssembler::assemble).collect(Collectors.toList()) : null);
    }

}

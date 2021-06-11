package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.UserDto;

import java.util.ArrayList;
import java.util.stream.Collectors;

public class UserDtoAssembler {

    public static UserDto assemble(String name, String email) {
        return new UserDto(null, name, email, null, false);
    }

    public static UserDto assemble(User user) {
        return new UserDto(user.getId(), user.getName(), user.getEmail(),
                user.getRoles() == null ? new ArrayList<>() :
                        user.getRoles().stream().map(RoleDtoAssembler::assemble).collect(Collectors.toList()),
                user.isSuperUser());
    }

    public static User disassemble(UserDto userDto) {
        return new User(userDto.getId(), userDto.getName(), userDto.getEmail(),
                userDto.getRoles() == null ? new ArrayList<>() :
                        userDto.getRoles().stream().map(RoleDtoAssembler::disassemble).collect(Collectors.toList()),
                false);
    }
}

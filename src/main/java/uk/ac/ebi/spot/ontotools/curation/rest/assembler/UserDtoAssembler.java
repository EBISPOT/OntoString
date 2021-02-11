package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.rest.dto.UserDto;

public class UserDtoAssembler {

    public static UserDto assemble(String name, String email) {
        return new UserDto(null, name, email, null);
    }
}

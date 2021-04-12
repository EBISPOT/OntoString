package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProjectUserDto implements Serializable {

    private static final long serialVersionUID = -6159143959677440040L;

    @NotEmpty
    @JsonProperty("roles")
    private final List<String> roles;

    @NotEmpty
    @JsonProperty("user")
    private final UserDto user;

    @JsonCreator
    public ProjectUserDto(@JsonProperty("user") UserDto user,
                          @JsonProperty("roles") List<String> roles) {
        this.user = user;
        this.roles = roles;
    }

    public List<String> getRoles() {
        return roles;
    }

    public UserDto getUser() {
        return user;
    }
}

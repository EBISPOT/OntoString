package uk.ac.ebi.spot.ontostring.rest.dto.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UserDto implements Serializable {

    private static final long serialVersionUID = 8940035463152694066L;

    @NotNull
    @JsonProperty("id")
    private final String id;

    @NotNull
    @JsonProperty("name")
    private final String name;

    @NotNull
    @JsonProperty("email")
    private final String email;

    @JsonProperty("roles")
    private final List<RoleDto> roles;

    @JsonProperty("superUser")
    private final Boolean superUser;

    @JsonCreator
    public UserDto(@JsonProperty("id") String id,
                   @JsonProperty("name") String name,
                   @JsonProperty("email") String email,
                   @JsonProperty("roles") List<RoleDto> roles,
                   @JsonProperty("superUser") Boolean superUser) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.roles = roles;
        this.superUser = superUser;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public List<RoleDto> getRoles() {
        return roles;
    }

    public Boolean getSuperUser() {
        return superUser;
    }
}

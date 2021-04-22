package uk.ac.ebi.spot.ontotools.curation.rest.dto.users;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class UserCreationDto implements Serializable {

    private static final long serialVersionUID = -7756602971327722980L;

    @NotNull
    @JsonProperty("name")
    private final String name;

    @NotNull
    @JsonProperty("email")
    private final String email;

    @JsonCreator
    public UserCreationDto(@JsonProperty("name") String name,
                           @JsonProperty("email") String email) {
        this.name = name;
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}

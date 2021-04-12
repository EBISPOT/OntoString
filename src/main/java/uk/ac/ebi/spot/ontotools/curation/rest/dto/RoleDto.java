package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class RoleDto implements Serializable {

    private static final long serialVersionUID = 1216570040619515074L;

    @JsonProperty("projectId")
    private final String projectId;

    @JsonProperty("role")
    private final String role;

    @JsonCreator
    public RoleDto(@JsonProperty("projectId") String projectId,
                   @JsonProperty("role") String role) {
        this.projectId = projectId;
        this.role = role;
    }

    public String getProjectId() {
        return projectId;
    }

    public String getRole() {
        return role;
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProjectExportStatusDto implements Serializable {

    private static final long serialVersionUID = -6467334898778590695L;

    @NotEmpty
    @JsonProperty("requestId")
    private final String requestId;

    @NotEmpty
    @JsonProperty("status")
    private final String status;

    @JsonCreator
    public ProjectExportStatusDto(@JsonProperty("requestId") String requestId,
                                  @JsonProperty("status") String status) {
        this.requestId = requestId;
        this.status = status;
    }

    public String getRequestId() {
        return requestId;
    }

    public String getStatus() {
        return status;
    }
}

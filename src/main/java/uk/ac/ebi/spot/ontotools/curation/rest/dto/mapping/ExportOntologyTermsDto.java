package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ExportOntologyTermsDto implements Serializable {

    private static final long serialVersionUID = -228520215297804714L;

    @NotEmpty
    @JsonProperty("status")
    private final String status;

    @NotEmpty
    @JsonProperty("context")
    private final String context;

    @JsonCreator
    public ExportOntologyTermsDto(@JsonProperty("status") String status,
                                  @JsonProperty("context") String context) {
        this.status = status;
        this.context = context;
    }

    public String getStatus() {
        return status;
    }

    public String getContext() {
        return context;
    }

}

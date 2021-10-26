package uk.ac.ebi.spot.ontostring.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ActionOntologyTermsDto implements Serializable {

    private static final long serialVersionUID = 6293157565470657048L;

    @NotEmpty
    @JsonProperty("status")
    private final String status;

    @NotEmpty
    @JsonProperty("context")
    private final String context;

    @NotEmpty
    @JsonProperty("comment")
    private final String comment;

    @JsonCreator
    public ActionOntologyTermsDto(@JsonProperty("status") String status,
                                  @JsonProperty("context") String context,
                                  @JsonProperty("comment") String comment) {
        this.status = status;
        this.context = context;
        this.comment = comment;
    }

    public String getStatus() {
        return status;
    }

    public String getContext() {
        return context;
    }

    public String getComment() {
        return comment;
    }
}

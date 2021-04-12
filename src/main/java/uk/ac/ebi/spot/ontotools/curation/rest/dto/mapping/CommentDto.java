package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProvenanceDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class CommentDto implements Serializable {

    private static final long serialVersionUID = -3975761372901822735L;

    @NotNull
    @JsonProperty("body")
    private final String body;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public CommentDto(@JsonProperty("body") String body,
                      @JsonProperty("created") ProvenanceDto created) {
        this.body = body;
        this.created = created;
    }

    public String getBody() {
        return body;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

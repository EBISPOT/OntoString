package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ReviewDto implements Serializable {

    private static final long serialVersionUID = -3975761372901822735L;

    @NotNull
    @JsonProperty("comment")
    private final String comment;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public ReviewDto(@JsonProperty("comment") String comment,
                     @JsonProperty("created") ProvenanceDto created) {
        this.comment = comment;
        this.created = created;
    }

    public String getComment() {
        return comment;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

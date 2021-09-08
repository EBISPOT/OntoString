package uk.ac.ebi.spot.ontostring.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontostring.rest.dto.ProvenanceDto;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ReviewDto implements Serializable {

    private static final long serialVersionUID = -3975761372901822735L;

    @JsonProperty("comment")
    private final String comment;

    @NotEmpty
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

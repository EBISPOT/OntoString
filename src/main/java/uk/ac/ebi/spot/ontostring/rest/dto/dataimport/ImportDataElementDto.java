package uk.ac.ebi.spot.ontostring.rest.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImportDataElementDto implements Serializable {

    private static final long serialVersionUID = -472202121082840265L;

    @JsonProperty("upstreamId")
    private final String upstreamId;

    @NotEmpty
    @JsonProperty("text")
    private final String text;

    @JsonProperty("context")
    private final String context;

    @JsonProperty("priority")
    private final Integer priority;

    @JsonCreator
    public ImportDataElementDto(@JsonProperty("text") String text,
                                @JsonProperty("upstreamId") String upstreamId,
                                @JsonProperty("context") String context,
                                @JsonProperty("priority") Integer priority) {
        this.upstreamId = upstreamId;
        this.text = text;
        this.context = context;
        this.priority = priority;
    }

    public String getText() {
        return text;
    }

    public String getUpstreamId() {
        return upstreamId;
    }

    public String getContext() {
        return context;
    }

    public Integer getPriority() {
        return priority;
    }
}

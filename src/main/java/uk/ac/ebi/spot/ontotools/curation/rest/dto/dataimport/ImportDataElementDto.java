package uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport;

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

    @JsonProperty("upstreamField")
    private final String upstreamField;

    @JsonCreator
    public ImportDataElementDto(@JsonProperty("text") String text,
                                @JsonProperty("upstreamId") String upstreamId,
                                @JsonProperty("upstreamField") String upstreamField) {
        this.upstreamId = upstreamId;
        this.text = text;
        this.upstreamField = upstreamField;
    }

    public String getText() {
        return text;
    }

    public String getUpstreamId() {
        return upstreamId;
    }

    public String getUpstreamField() {
        return upstreamField;
    }
}

package uk.ac.ebi.spot.ontostring.rest.dto.oxo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OXOResponseDto implements Serializable {

    private static final long serialVersionUID = -9198144318907033616L;

    @JsonProperty("_embedded")
    private final OXOEmbeddedDto embedded;

    @JsonCreator
    public OXOResponseDto(@JsonProperty("_embedded") OXOEmbeddedDto embedded) {
        this.embedded = embedded;
    }

    public OXOEmbeddedDto getEmbedded() {
        return embedded;
    }
}

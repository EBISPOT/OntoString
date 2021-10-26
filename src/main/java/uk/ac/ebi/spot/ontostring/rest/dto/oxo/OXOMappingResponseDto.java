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
public final class OXOMappingResponseDto implements Serializable {

    private static final long serialVersionUID = -9198144318907033616L;

    @JsonProperty("curie")
    private final String curie;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("targetPrefix")
    private final String targetPrefix;

    @JsonCreator
    public OXOMappingResponseDto(@JsonProperty("curie") String curie,
                                 @JsonProperty("label") String label,
                                 @JsonProperty("targetPrefix") String targetPrefix) {
        this.curie = curie;
        this.label = label;
        this.targetPrefix = targetPrefix;
    }

    public String getCurie() {
        return curie;
    }

    public String getLabel() {
        return label;
    }

    public String getTargetPrefix() {
        return targetPrefix;
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.dto.zooma;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class ZoomaResponseDto implements Serializable {

    private static final long serialVersionUID = 5688769708230750868L;

    @JsonProperty("semanticTags")
    private final List<String> semanticTags;

    @JsonProperty("confidence")
    private final String confidence;

    @JsonCreator
    public ZoomaResponseDto(@JsonProperty("semanticTags") List<String> semanticTags,
                            @JsonProperty("confidence") String confidence) {
        this.semanticTags = semanticTags;
        this.confidence = confidence;
    }

    public List<String> getSemanticTags() {
        return semanticTags;
    }

    public String getConfidence() {
        return confidence;
    }
}

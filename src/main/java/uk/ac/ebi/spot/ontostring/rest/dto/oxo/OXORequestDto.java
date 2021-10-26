package uk.ac.ebi.spot.ontostring.rest.dto.oxo;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OXORequestDto implements Serializable {

    private static final long serialVersionUID = -9198144318907033616L;

    @JsonProperty("ids")
    private final List<String> ids;

    @JsonProperty("mappingTarget")
    private final List<String> mappingTarget;

    @JsonProperty("distance")
    private final int distance;

    @JsonCreator
    public OXORequestDto(@JsonProperty("ids") List<String> ids,
                         @JsonProperty("mappingTarget") List<String> mappingTarget,
                         @JsonProperty("distance") int distance) {
        this.ids = ids;
        this.mappingTarget = mappingTarget;
        this.distance = distance;
    }

    public List<String> getIds() {
        return ids;
    }

    public List<String> getMappingTarget() {
        return mappingTarget;
    }

    public int getDistance() {
        return distance;
    }
}

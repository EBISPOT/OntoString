package uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo;

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
public final class OXOSearchResultDto implements Serializable {

    private static final long serialVersionUID = -9198144318907033616L;

    @JsonProperty("mappingResponseList")
    private final List<OXOMappingResponseDto> mappingResponse;

    @JsonCreator
    public OXOSearchResultDto(@JsonProperty("mappingResponseList") List<OXOMappingResponseDto> mappingResponse) {
        this.mappingResponse = mappingResponse;
    }

    public List<OXOMappingResponseDto> getMappingResponse() {
        return mappingResponse;
    }
}

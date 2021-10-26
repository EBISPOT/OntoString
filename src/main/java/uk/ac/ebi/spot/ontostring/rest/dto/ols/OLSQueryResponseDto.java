package uk.ac.ebi.spot.ontostring.rest.dto.ols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OLSQueryResponseDto implements Serializable {

    private static final long serialVersionUID = 6519860430552605535L;

    @JsonProperty("response")
    private final OLSQueryResponseEntryDto response;

    @JsonCreator
    public OLSQueryResponseDto(@JsonProperty("response") OLSQueryResponseEntryDto response) {
        this.response = response;
    }

    public OLSQueryResponseEntryDto getResponse() {
        return response;
    }
}

package uk.ac.ebi.spot.ontostring.rest.dto.ols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OLSQueryResultDto implements Serializable {

    private static final long serialVersionUID = 6907460080411746195L;

    @JsonProperty("results")
    private final Map<String, List<OLSQueryDocDto>> results;

    @JsonCreator
    public OLSQueryResultDto(@JsonProperty("results") Map<String, List<OLSQueryDocDto>> results) {
        this.results = results;
    }

    public Map<String, List<OLSQueryDocDto>> getResults() {
        return results;
    }
}

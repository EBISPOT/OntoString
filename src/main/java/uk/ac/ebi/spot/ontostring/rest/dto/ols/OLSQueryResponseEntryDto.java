package uk.ac.ebi.spot.ontostring.rest.dto.ols;

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
public final class OLSQueryResponseEntryDto implements Serializable {

    private static final long serialVersionUID = 7846676429881522819L;

    @JsonProperty("numFound")
    private final Integer numFound;

    @JsonProperty("start")
    private final Integer start;

    @JsonProperty("docs")
    private final List<OLSQueryDocDto> docs;

    @JsonCreator
    public OLSQueryResponseEntryDto(@JsonProperty("numFound") Integer numFound,
                                    @JsonProperty("start") Integer start,
                                    @JsonProperty("docs") List<OLSQueryDocDto> docs) {
        this.numFound = numFound;
        this.start = start;
        this.docs = docs;
    }

    public Integer getNumFound() {
        return numFound;
    }

    public Integer getStart() {
        return start;
    }

    public List<OLSQueryDocDto> getDocs() {
        return docs;
    }
}

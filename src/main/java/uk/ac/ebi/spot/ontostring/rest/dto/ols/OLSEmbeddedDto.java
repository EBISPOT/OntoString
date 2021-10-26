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
public final class OLSEmbeddedDto implements Serializable {

    private static final long serialVersionUID = -2333575143636379574L;

    @JsonProperty("terms")
    private final List<OLSTermDto> terms;

    @JsonCreator
    public OLSEmbeddedDto(@JsonProperty("terms") List<OLSTermDto> terms) {
        this.terms = terms;
    }

    public List<OLSTermDto> getTerms() {
        return terms;
    }
}

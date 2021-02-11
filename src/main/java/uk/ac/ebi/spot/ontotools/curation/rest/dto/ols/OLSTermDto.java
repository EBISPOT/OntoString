package uk.ac.ebi.spot.ontotools.curation.rest.dto.ols;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public final class OLSTermDto implements Serializable {

    private static final long serialVersionUID = -6048583493585168328L;

    @JsonProperty("iri")
    private final String iri;

    @JsonProperty("obo_id")
    private final String curie;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("is_obsolete")
    private final Boolean obsolete;

    @JsonCreator
    public OLSTermDto(@JsonProperty("iri") String iri,
                      @JsonProperty("obo_id") String curie,
                      @JsonProperty("label") String label,
                      @JsonProperty("is_obsolete") Boolean obsolete) {
        this.iri = iri;
        this.curie = curie;
        this.label = label;
        this.obsolete = obsolete;
    }

    public String getIri() {
        return iri;
    }

    public String getCurie() {
        return curie;
    }

    public String getLabel() {
        return label;
    }

    public Boolean getObsolete() {
        return obsolete;
    }
}
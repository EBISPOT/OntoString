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
public final class OLSTermDto implements Serializable {

    private static final long serialVersionUID = -6048583493585168328L;

    @JsonProperty("iri")
    private final String iri;

    @JsonProperty("obo_id")
    private final String curie;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("ontology_name")
    private final String ontologyName;

    @JsonProperty("is_obsolete")
    private final Boolean obsolete;

    @JsonProperty("is_defining_ontology")
    private final Boolean definingOntology;

    @JsonCreator
    public OLSTermDto(@JsonProperty("iri") String iri,
                      @JsonProperty("obo_id") String curie,
                      @JsonProperty("label") String label,
                      @JsonProperty("ontology_name") String ontologyName,
                      @JsonProperty("is_obsolete") Boolean obsolete,
                      @JsonProperty("is_defining_ontology") Boolean definingOntology) {
        this.iri = iri;
        this.curie = curie;
        this.label = label;
        this.ontologyName = ontologyName;
        this.obsolete = obsolete;
        this.definingOntology = definingOntology;
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

    public Boolean getDefiningOntology() {
        return definingOntology;
    }
}

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
public final class OLSQueryDocDto implements Serializable {

    private static final long serialVersionUID = -1340048985906827319L;

    @JsonProperty("iri")
    private final String iri;

    @JsonProperty("obo_id")
    private final String curie;

    @JsonProperty("label")
    private final String label;

    @JsonProperty("description")
    private final List<String> description;

    @JsonProperty("ontology_name")
    private final String ontologyName;

    @JsonCreator
    public OLSQueryDocDto(@JsonProperty("iri") String iri,
                          @JsonProperty("obo_id") String curie,
                          @JsonProperty("label") String label,
                          @JsonProperty("description") List<String> description,
                          @JsonProperty("ontology_name") String ontologyName) {
        this.iri = iri;
        this.curie = curie;
        this.label = label;
        this.description = description;
        this.ontologyName = ontologyName;
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

    public List<String> getDescription() {
        return description;
    }

    public String getOntologyName() {
        return ontologyName;
    }
}

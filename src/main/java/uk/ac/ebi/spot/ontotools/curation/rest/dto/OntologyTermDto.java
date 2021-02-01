package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OntologyTermDto implements Serializable {

    private static final long serialVersionUID = 714105630780606515L;

    @NotNull
    @JsonProperty("curie")
    private final String curie;

    @NotNull
    @JsonProperty("iri")
    private final String iri;

    @NotNull
    @JsonProperty("label")
    private final String label;

    @NotNull
    @JsonProperty("status")
    private final String status;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("crossRefs")
    private final String crossRefs;

    @JsonCreator
    public OntologyTermDto(@JsonProperty("curie") String curie,
                           @JsonProperty("iri") String iri,
                           @JsonProperty("label") String label,
                           @JsonProperty("status") String status,
                           @JsonProperty("description") String description,
                           @JsonProperty("crossRefs") String crossRefs) {
        this.curie = curie;
        this.iri = iri;
        this.label = label;
        this.status = status;
        this.description = description;
        this.crossRefs = crossRefs;
    }

    public String getCurie() {
        return curie;
    }

    public String getIri() {
        return iri;
    }

    public String getLabel() {
        return label;
    }

    public String getStatus() {
        return status;
    }

    public String getDescription() {
        return description;
    }

    public String getCrossRefs() {
        return crossRefs;
    }
}

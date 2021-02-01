package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MappingDto implements Serializable {

    private static final long serialVersionUID = -2548737672325162378L;

    @NotNull
    @JsonProperty("id")
    private final String id;

    @NotNull
    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public MappingDto(@JsonProperty("id") String id,
                      @JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm,
                      @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.ontologyTerm = ontologyTerm;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

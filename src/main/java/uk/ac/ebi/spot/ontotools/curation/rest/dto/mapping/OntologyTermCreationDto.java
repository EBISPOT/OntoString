package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OntologyTermCreationDto implements Serializable {

    private static final long serialVersionUID = 4164035957804875140L;

    @NotEmpty
    @JsonProperty("context")
    private final String context;

    @NotEmpty
    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @JsonCreator
    public OntologyTermCreationDto(@JsonProperty("context") String context,
                                   @JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm) {
        this.context = context;
        this.ontologyTerm = ontologyTerm;
    }

    public String getContext() {
        return context;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }
}

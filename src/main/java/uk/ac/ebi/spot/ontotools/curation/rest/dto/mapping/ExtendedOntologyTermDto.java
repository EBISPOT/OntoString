package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ExtendedOntologyTermDto implements Serializable {

    private static final long serialVersionUID = 1013513082083495371L;

    @JsonProperty("entities")
    private final List<ShortEntityInfoDto> entities;

    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @JsonCreator
    public ExtendedOntologyTermDto(@JsonProperty("entities") List<ShortEntityInfoDto> entities,
                                   @JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm) {
        this.entities = entities;
        this.ontologyTerm = ontologyTerm;
    }

    public List<ShortEntityInfoDto> getEntities() {
        return entities;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }
}

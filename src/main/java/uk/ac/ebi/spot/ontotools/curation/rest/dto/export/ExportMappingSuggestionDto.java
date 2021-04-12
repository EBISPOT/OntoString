package uk.ac.ebi.spot.ontotools.curation.rest.dto.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProvenanceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ExportMappingSuggestionDto implements Serializable {

    private static final long serialVersionUID = 3552462400615112894L;

    @NotNull
    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public ExportMappingSuggestionDto(@JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm,
                                      @JsonProperty("created") ProvenanceDto created) {
        this.ontologyTerm = ontologyTerm;
        this.created = created;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

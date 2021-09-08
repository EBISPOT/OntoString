package uk.ac.ebi.spot.ontostring.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontostring.rest.dto.ProvenanceDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MappingSuggestionDto implements Serializable {

    private static final long serialVersionUID = -4565784727352793191L;

    @NotNull
    @JsonProperty("id")
    private final String id;

    @JsonProperty("entityId")
    private final String entityId;

    @NotNull
    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public MappingSuggestionDto(@JsonProperty("id") String id,
                                @JsonProperty("entityId") String entityId,
                                @JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm,
                                @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.entityId = entityId;
        this.ontologyTerm = ontologyTerm;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }

    public String getEntityId() {
        return entityId;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

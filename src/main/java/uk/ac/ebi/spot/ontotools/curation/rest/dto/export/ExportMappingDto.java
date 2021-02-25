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
public final class ExportMappingDto implements Serializable {

    private static final long serialVersionUID = -6638221277434507289L;

    @NotNull
    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @JsonProperty("reviewed")
    private final boolean reviewed;

    @JsonProperty("status")
    private final String status;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public ExportMappingDto(@JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm,
                            @JsonProperty("reviewed") boolean reviewed,
                            @JsonProperty("status") String status,
                            @JsonProperty("created") ProvenanceDto created) {
        this.ontologyTerm = ontologyTerm;
        this.reviewed = reviewed;
        this.status = status;
        this.created = created;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public String getStatus() {
        return status;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

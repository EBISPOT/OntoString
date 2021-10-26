package uk.ac.ebi.spot.ontostring.rest.dto.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.OntologyTermDto;
import uk.ac.ebi.spot.ontostring.rest.dto.ProvenanceDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ExportMappingDto implements Serializable {

    private static final long serialVersionUID = -6638221277434507289L;

    @NotNull
    @JsonProperty("ontologyTerms")
    private final List<OntologyTermDto> ontologyTerms;

    @JsonProperty("reviewed")
    private final boolean reviewed;

    @JsonProperty("status")
    private final String status;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public ExportMappingDto(@JsonProperty("ontologyTerms") List<OntologyTermDto> ontologyTerms,
                            @JsonProperty("reviewed") boolean reviewed,
                            @JsonProperty("status") String status,
                            @JsonProperty("created") ProvenanceDto created) {
        this.ontologyTerms = ontologyTerms;
        this.reviewed = reviewed;
        this.status = status;
        this.created = created;
    }

    public List<OntologyTermDto> getOntologyTerms() {
        return ontologyTerms;
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

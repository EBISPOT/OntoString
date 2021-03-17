package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MappingCreationDto implements Serializable {

    private static final long serialVersionUID = -2530149590750902127L;

    @NotNull
    @JsonProperty("entityId")
    private final String entityId;

    @NotNull
    @JsonProperty("ontologyTerms")
    private final List<OntologyTermDto> ontologyTerms;

    @JsonCreator
    public MappingCreationDto(@JsonProperty("entityId") String entityId,
                              @JsonProperty("ontologyTerms") List<OntologyTermDto> ontologyTerms) {
        this.entityId = entityId;
        this.ontologyTerms = ontologyTerms;
    }


    public List<OntologyTermDto> getOntologyTerms() {
        return ontologyTerms;
    }

    public String getEntityId() {
        return entityId;
    }
}

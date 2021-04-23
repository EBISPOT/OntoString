package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.Map;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class OntologyTermStatsDto implements Serializable {

    private static final long serialVersionUID = 6487875738611734064L;

    @NotEmpty
    @JsonProperty("stats")
    private final Map<String, Integer> stats;

    @JsonCreator
    public OntologyTermStatsDto(@JsonProperty("stats") Map<String, Integer> stats) {
        this.stats = stats;
    }

    public Map<String, Integer> getStats() {
        return stats;
    }
}

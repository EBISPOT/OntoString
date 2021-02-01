package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class TraitDto implements Serializable {

    private static final long serialVersionUID = 714105630780606515L;

    @NotNull
    @JsonProperty("id")
    private final String id;

    @NotNull
    @JsonProperty("name")
    private final String name;

    @JsonProperty("currentMapping")
    private final MappingDto currentMapping;

    @JsonProperty("noSourceRecords")
    private final int noSourceRecords;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @NotNull
    @JsonProperty("lastUpdated")
    private final ProvenanceDto lastUpdated;

    @JsonCreator
    public TraitDto(@JsonProperty("id") String id,
                    @JsonProperty("name") String name,
                    @JsonProperty("currentMapping") MappingDto currentMapping,
                    @JsonProperty("noSourceRecords") int noSourceRecords,
                    @JsonProperty("created") ProvenanceDto created,
                    @JsonProperty("lastUpdated") ProvenanceDto lastUpdated) {
        this.id = id;
        this.name = name;
        this.currentMapping = currentMapping;
        this.noSourceRecords = noSourceRecords;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public MappingDto getCurrentMapping() {
        return currentMapping;
    }

    public int getNoSourceRecords() {
        return noSourceRecords;
    }

    public ProvenanceDto getCreated() {
        return created;
    }

    public ProvenanceDto getLastUpdated() {
        return lastUpdated;
    }
}

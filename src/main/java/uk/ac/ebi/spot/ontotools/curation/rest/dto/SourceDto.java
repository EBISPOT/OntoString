package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SourceDto implements Serializable {

    private static final long serialVersionUID = -452026015300129980L;

    @NotEmpty
    @JsonProperty("id")
    private final String id;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("uri")
    private final String uri;

    @JsonProperty("type")
    private final String type;

    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonProperty("lastUpdated")
    private final ProvenanceDto lastUpdated;

    @JsonCreator
    public SourceDto(@JsonProperty("id") String id,
                     @JsonProperty("name") String name,
                     @JsonProperty("description") String description,
                     @JsonProperty("uri") String uri,
                     @JsonProperty("type") String type,
                     @JsonProperty("created") ProvenanceDto created,
                     @JsonProperty("lastUpdated") ProvenanceDto lastUpdated) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.type = type;
        this.created = created;
        this.lastUpdated = lastUpdated;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUri() {
        return uri;
    }

    public String getType() {
        return type;
    }

    public ProvenanceDto getCreated() {
        return created;
    }

    public ProvenanceDto getLastUpdated() {
        return lastUpdated;
    }
}

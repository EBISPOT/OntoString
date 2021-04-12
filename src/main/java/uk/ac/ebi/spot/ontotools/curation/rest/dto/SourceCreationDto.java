package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SourceCreationDto implements Serializable {

    private static final long serialVersionUID = -452026015300129980L;

    @NotEmpty
    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("uri")
    private final String uri;

    @NotEmpty
    @JsonProperty("type")
    private final String type;

    @JsonCreator
    public SourceCreationDto(@JsonProperty("name") String name,
                             @JsonProperty("description") String description,
                             @JsonProperty("uri") String uri,
                             @JsonProperty("type") String type) {
        this.name = name;
        this.description = description;
        this.uri = uri;
        this.type = type;
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
}

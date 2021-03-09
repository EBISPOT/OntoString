package uk.ac.ebi.spot.ontotools.curation.rest.dto.audit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MetadataEntryDto implements Serializable {

    private static final long serialVersionUID = -2731589237887840195L;

    @NotEmpty
    @JsonProperty("key")
    private final String key;

    @NotEmpty
    @JsonProperty("value")
    private final String value;

    @JsonCreator
    public MetadataEntryDto(@JsonProperty("key") String key,
                            @JsonProperty("value") String value) {
        this.key = key;
        this.value = value;
    }

    public String getKey() {
        return key;
    }

    public String getValue() {
        return value;
    }
}

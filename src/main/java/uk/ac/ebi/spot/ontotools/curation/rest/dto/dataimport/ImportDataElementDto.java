package uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImportDataElementDto implements Serializable {

    private static final long serialVersionUID = -472202121082840265L;

    @JsonProperty("baseId")
    private final String baseId;

    @NotEmpty
    @JsonProperty("text")
    private final String text;

    @JsonProperty("baseField")
    private final String baseField;

    @JsonCreator
    public ImportDataElementDto(@JsonProperty("text") String text,
                                @JsonProperty("baseId") String baseId,
                                @JsonProperty("baseField") String baseField) {
        this.baseId = baseId;
        this.text = text;
        this.baseField = baseField;
    }

    public String getBaseId() {
        return baseId;
    }

    public String getText() {
        return text;
    }

    public String getBaseField() {
        return baseField;
    }
}

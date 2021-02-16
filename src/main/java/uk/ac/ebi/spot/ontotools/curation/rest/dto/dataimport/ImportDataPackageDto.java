package uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ImportDataPackageDto implements Serializable {

    private static final long serialVersionUID = 5122911468770924907L;

    @NotEmpty
    @JsonProperty("data")
    private final List<ImportDataElementDto> data;

    @JsonCreator
    public ImportDataPackageDto(@JsonProperty("text") List<ImportDataElementDto> data) {
        this.data = data;
    }

    public List<ImportDataElementDto> getData() {
        return data;
    }
}

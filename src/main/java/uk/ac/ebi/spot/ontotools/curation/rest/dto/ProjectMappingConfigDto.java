package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProjectMappingConfigDto implements Serializable {

    private static final long serialVersionUID = 5092703110421044505L;

    @NotEmpty
    @JsonProperty("field")
    private final String field;

    @NotEmpty
    @JsonProperty("mappingList")
    private final List<String> mappingList;

    @JsonCreator
    public ProjectMappingConfigDto(@JsonProperty("field") String field,
                                   @JsonProperty("mappingList") List<String> mappingList) {
        this.field = field;
        this.mappingList = mappingList;
    }

    public String getField() {
        return field;
    }

    public List<String> getMappingList() {
        return mappingList;
    }
}

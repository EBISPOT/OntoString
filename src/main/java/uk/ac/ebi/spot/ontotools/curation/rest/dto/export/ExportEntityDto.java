package uk.ac.ebi.spot.ontotools.curation.rest.dto.export;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ExportEntityDto implements Serializable {

    private static final long serialVersionUID = 1657559863177266372L;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("baseId")
    private final String baseId;

    @JsonProperty("baseField")
    private final String baseField;

    @JsonProperty("mappingSuggestions")
    private final List<ExportMappingSuggestionDto> mappingSuggestions;

    @JsonProperty("mappings")
    private final List<ExportMappingDto> mappings;

    @JsonCreator
    public ExportEntityDto(@JsonProperty("name") String name,
                           @JsonProperty("baseId") String baseId,
                           @JsonProperty("baseField") String baseField,
                           @JsonProperty("mappingSuggestions") List<ExportMappingSuggestionDto> mappingSuggestions,
                           @JsonProperty("mappings") List<ExportMappingDto> mappings) {
        this.name = name;
        this.baseId = baseId;
        this.baseField = baseField;
        this.mappingSuggestions = mappingSuggestions;
        this.mappings = mappings;
    }

    public String getName() {
        return name;
    }

    public String getBaseId() {
        return baseId;
    }

    public String getBaseField() {
        return baseField;
    }

    public List<ExportMappingSuggestionDto> getMappingSuggestions() {
        return mappingSuggestions;
    }

    public List<ExportMappingDto> getMappings() {
        return mappings;
    }
}

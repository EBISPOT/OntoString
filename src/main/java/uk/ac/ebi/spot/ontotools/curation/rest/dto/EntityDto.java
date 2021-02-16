package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class EntityDto implements Serializable {

    private static final long serialVersionUID = 5798967900389052490L;

    @NotNull
    @JsonProperty("id")
    private final String id;

    @JsonProperty("source")
    private final SourceDto source;

    @JsonProperty("name")
    private final String name;

    @JsonProperty("baseId")
    private final String baseId;

    @JsonProperty("baseField")
    private final String baseField;

    @JsonProperty("mappingStatus")
    private final String mappingStatus;

    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonProperty("mappingSuggestions")
    private final List<MappingSuggestionDto> mappingSuggestions;

    @JsonProperty("mappings")
    private final List<MappingDto> mappings;

    @JsonCreator
    public EntityDto(@JsonProperty("id") String id,
                     @JsonProperty("source") SourceDto source,
                     @JsonProperty("name") String name,
                     @JsonProperty("baseId") String baseId,
                     @JsonProperty("baseField") String baseField,
                     @JsonProperty("mappingStatus") String mappingStatus,
                     @JsonProperty("mappingSuggestions") List<MappingSuggestionDto> mappingSuggestions,
                     @JsonProperty("mappings") List<MappingDto> mappings,
                     @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.baseId = baseId;
        this.baseField = baseField;
        this.mappingStatus = mappingStatus;
        this.mappingSuggestions = mappingSuggestions;
        this.mappings = mappings;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public SourceDto getSource() {
        return source;
    }

    public String getName() {
        return name;
    }

    public String getMappingStatus() {
        return mappingStatus;
    }

    public ProvenanceDto getCreated() {
        return created;
    }

    public List<MappingSuggestionDto> getMappingSuggestions() {
        return mappingSuggestions;
    }

    public List<MappingDto> getMappings() {
        return mappings;
    }

    public String getBaseId() {
        return baseId;
    }

    public String getBaseField() {
        return baseField;
    }
}

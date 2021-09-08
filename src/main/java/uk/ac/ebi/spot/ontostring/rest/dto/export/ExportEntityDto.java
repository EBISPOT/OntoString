package uk.ac.ebi.spot.ontostring.rest.dto.export;

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

    @JsonProperty("upstreamId")
    private final String upstreamId;

    @JsonProperty("context")
    private final String context;

    @JsonProperty("priority")
    private final Integer priority;

    @JsonProperty("mappingSuggestions")
    private final List<ExportMappingSuggestionDto> mappingSuggestions;

    @JsonProperty("mapping")
    private final ExportMappingDto mapping;

    @JsonCreator
    public ExportEntityDto(@JsonProperty("name") String name,
                           @JsonProperty("upstreamId") String upstreamId,
                           @JsonProperty("context") String context,
                           @JsonProperty("priority") Integer priority,
                           @JsonProperty("mappingSuggestions") List<ExportMappingSuggestionDto> mappingSuggestions,
                           @JsonProperty("mapping") ExportMappingDto mapping) {
        this.name = name;
        this.upstreamId = upstreamId;
        this.context = context;
        this.mappingSuggestions = mappingSuggestions;
        this.mapping = mapping;
        this.priority = priority;
    }

    public String getName() {
        return name;
    }


    public String getUpstreamId() {
        return upstreamId;
    }

    public String getContext() {
        return context;
    }

    public List<ExportMappingSuggestionDto> getMappingSuggestions() {
        return mappingSuggestions;
    }

    public ExportMappingDto getMapping() {
        return mapping;
    }

    public Integer getPriority() {
        return priority;
    }
}

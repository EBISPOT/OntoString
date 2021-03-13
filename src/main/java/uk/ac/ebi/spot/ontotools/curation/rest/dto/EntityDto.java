package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.AuditEntryDto;
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

    @JsonProperty("upstreamId")
    private final String upstreamId;

    @JsonProperty("context")
    private final String context;

    @JsonProperty("mappingStatus")
    private final String mappingStatus;

    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonProperty("mappingSuggestions")
    private final List<MappingSuggestionDto> mappingSuggestions;

    @JsonProperty("mapping")
    private final MappingDto mapping;

    @JsonProperty("auditTrail")
    private final List<AuditEntryDto> auditTrail;

    @JsonCreator
    public EntityDto(@JsonProperty("id") String id,
                     @JsonProperty("source") SourceDto source,
                     @JsonProperty("name") String name,
                     @JsonProperty("upstreamId") String upstreamId,
                     @JsonProperty("context") String context,
                     @JsonProperty("mappingStatus") String mappingStatus,
                     @JsonProperty("mappingSuggestions") List<MappingSuggestionDto> mappingSuggestions,
                     @JsonProperty("mapping") MappingDto mapping,
                     @JsonProperty("auditTrail") List<AuditEntryDto> auditTrail,
                     @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.source = source;
        this.name = name;
        this.upstreamId = upstreamId;
        this.context = context;
        this.mappingStatus = mappingStatus;
        this.mappingSuggestions = mappingSuggestions;
        this.mapping = mapping;
        this.auditTrail = auditTrail;
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


    public MappingDto getMapping() {
        return mapping;
    }

    public String getUpstreamId() {
        return upstreamId;
    }

    public String getContext() {
        return context;
    }

    public List<AuditEntryDto> getAuditTrail() {
        return auditTrail;
    }
}

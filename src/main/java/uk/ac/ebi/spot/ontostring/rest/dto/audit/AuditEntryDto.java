package uk.ac.ebi.spot.ontostring.rest.dto.audit;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import uk.ac.ebi.spot.ontostring.util.JsonJodaDateTimeDeserializer;
import uk.ac.ebi.spot.ontostring.util.JsonJodaDateTimeSerializer;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class AuditEntryDto implements Serializable {

    private static final long serialVersionUID = 1202550557817376005L;

    @NotEmpty
    @JsonProperty("action")
    private final String action;

    @NotEmpty
    @JsonProperty("user")
    private final AuditUserDto user;

    @JsonProperty("metadata")
    private final List<MetadataEntryDto> metadata;

    @NotEmpty
    @JsonProperty("timestamp")
    @JsonSerialize(using = JsonJodaDateTimeSerializer.class)
    private final DateTime timestamp;


    @JsonCreator
    public AuditEntryDto(@JsonProperty("action") String action,
                         @JsonProperty("user") AuditUserDto user,
                         @JsonProperty("metadata") List<MetadataEntryDto> metadata,
                         @JsonProperty("timestamp") @JsonDeserialize(using = JsonJodaDateTimeDeserializer.class) DateTime timestamp) {
        this.action = action;
        this.user = user;
        this.metadata = metadata;
        this.timestamp = timestamp;
    }

    public String getAction() {
        return action;
    }

    public AuditUserDto getUser() {
        return user;
    }

    public List<MetadataEntryDto> getMetadata() {
        return metadata;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }
}

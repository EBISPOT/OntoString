package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.EqualsAndHashCode;
import org.joda.time.DateTime;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.users.UserDto;
import uk.ac.ebi.spot.ontotools.curation.util.JsonJodaDateTimeDeserializer;
import uk.ac.ebi.spot.ontotools.curation.util.JsonJodaDateTimeSerializer;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProvenanceDto implements Serializable {

    private static final long serialVersionUID = -527759108725584128L;

    @NotEmpty
    @JsonSerialize(using = JsonJodaDateTimeSerializer.class)
    @JsonProperty("timestamp")
    private final DateTime timestamp;

    @NotEmpty
    @JsonProperty("user")
    private final UserDto user;

    @JsonCreator
    public ProvenanceDto(@JsonProperty("user") UserDto user,
                         @JsonProperty("timestamp") @JsonDeserialize(using = JsonJodaDateTimeDeserializer.class) DateTime timestamp) {
        this.user = user;
        this.timestamp = timestamp;
    }

    public DateTime getTimestamp() {
        return timestamp;
    }

    public UserDto getUser() {
        return user;
    }

}

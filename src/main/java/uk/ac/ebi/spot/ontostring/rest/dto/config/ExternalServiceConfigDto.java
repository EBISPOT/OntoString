package uk.ac.ebi.spot.ontostring.rest.dto.config;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.Map;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ExternalServiceConfigDto implements Serializable {

    private static final long serialVersionUID = 4777554475058064805L;

    @NotNull
    @JsonProperty("serviceName")
    private final String serviceName;

    @JsonProperty("aliases")
    private final Map<String, String> aliases;

    @JsonCreator
    public ExternalServiceConfigDto(@JsonProperty("serviceName") String serviceName,
                                    @JsonProperty("aliases") Map<String, String> aliases) {
        this.serviceName = serviceName;
        this.aliases = aliases;
    }

    public String getServiceName() {
        return serviceName;
    }

    public Map<String, String> getAliases() {
        return aliases;
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProjectContextDto implements Serializable {

    private static final long serialVersionUID = -4397444940725422977L;

    @NotNull
    @Pattern(regexp = "^[a-zA-Z0-9]+$", message = "Invalid context name.")
    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("datasources")
    private final List<String> datasources;

    @JsonProperty("ontologies")
    private final List<String> ontologies;

    @JsonProperty("preferredMappingOntologies")
    private final List<String> preferredMappingOntologies;

    @JsonCreator
    public ProjectContextDto(@JsonProperty("name") String name,
                             @JsonProperty("description") String description,
                             @JsonProperty("datasources") List<String> datasources,
                             @JsonProperty("ontologies") List<String> ontologies,
                             @JsonProperty("preferredMappingOntologies") List<String> preferredMappingOntologies) {
        this.name = name;
        this.description = description;
        this.datasources = datasources;
        this.ontologies = ontologies;
        this.preferredMappingOntologies = preferredMappingOntologies;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<String> getDatasources() {
        return datasources;
    }

    public List<String> getOntologies() {
        return ontologies;
    }

    public List<String> getPreferredMappingOntologies() {
        return preferredMappingOntologies;
    }
}
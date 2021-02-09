package uk.ac.ebi.spot.ontotools.curation.rest.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProjectCreationDto implements Serializable {

    private static final long serialVersionUID = -4397444940725422977L;

    @NotNull
    @JsonProperty("name")
    private final String name;

    @JsonProperty("description")
    private final String description;

    @JsonProperty("datasources")
    private final List<String> datasources;

    @JsonProperty("ontologies")
    private final List<String> ontologies;

    @JsonProperty("preferredMappingOntology")
    private final String preferredMappingOntology;

    @JsonCreator
    public ProjectCreationDto(@JsonProperty("name") String name,
                              @JsonProperty("description") String description,
                              @JsonProperty("datasources") List<String> datasources,
                              @JsonProperty("ontologies") List<String> ontologies,
                              @JsonProperty("preferredMappingOntology") String preferredMappingOntology) {
        this.name = name;
        this.description = description;
        this.datasources = datasources;
        this.ontologies = ontologies;
        this.preferredMappingOntology = preferredMappingOntology;
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

    public String getPreferredMappingOntology() {
        return preferredMappingOntology;
    }

    public List<String> getOntologies() {
        return ontologies;
    }
}

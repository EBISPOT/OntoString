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
public final class ProjectDto implements Serializable {

    private static final long serialVersionUID = -4397444940725422977L;

    @NotNull
    @JsonProperty("id")
    private final String id;

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

    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public ProjectDto(@JsonProperty("id") String id,
                      @JsonProperty("name") String name,
                      @JsonProperty("description") String description,
                      @JsonProperty("datasources") List<String> datasources,
                      @JsonProperty("ontologies") List<String> ontologies,
                      @JsonProperty("preferredMappingOntology") String preferredMappingOntology,
                      @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.datasources = datasources;
        this.ontologies = ontologies;
        this.preferredMappingOntology = preferredMappingOntology;
        this.created = created;
    }

    public String getId() {
        return id;
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

    public String getPreferredMappingOntology() {
        return preferredMappingOntology;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}
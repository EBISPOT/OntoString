package uk.ac.ebi.spot.ontostring.rest.dto.project;

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

    @JsonProperty("numberOfReviewsRequired")
    private final Integer numberOfReviewsRequired;

    @JsonProperty("datasources")
    private final List<String> datasources;

    @JsonProperty("ontologies")
    private final List<String> ontologies;

    @JsonProperty("preferredMappingOntologies")
    private final List<String> preferredMappingOntologies;

    @JsonProperty("graphRestriction")
    private final ProjectContextGraphRestrictionDto graphRestriction;

    @JsonCreator
    public ProjectCreationDto(@JsonProperty("name") String name,
                              @JsonProperty("description") String description,
                              @JsonProperty("numberOfReviewsRequired") Integer numberOfReviewsRequired,
                              @JsonProperty("datasources") List<String> datasources,
                              @JsonProperty("ontologies") List<String> ontologies,
                              @JsonProperty("preferredMappingOntologies") List<String> preferredMappingOntologies,
                              @JsonProperty("graphRestriction") ProjectContextGraphRestrictionDto graphRestriction) {
        this.name = name;
        this.description = description;
        this.numberOfReviewsRequired = numberOfReviewsRequired;
        this.datasources = datasources;
        this.ontologies = ontologies;
        this.preferredMappingOntologies = preferredMappingOntologies;
        this.graphRestriction = graphRestriction;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public Integer getNumberOfReviewsRequired() {
        return numberOfReviewsRequired;
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

    public ProjectContextGraphRestrictionDto getGraphRestriction() {
        return graphRestriction;
    }
}

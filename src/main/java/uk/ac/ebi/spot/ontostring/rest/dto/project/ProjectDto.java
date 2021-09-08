package uk.ac.ebi.spot.ontostring.rest.dto.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontostring.rest.dto.ProvenanceDto;

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

    @JsonProperty("contexts")
    private final List<ProjectContextDto> contexts;

    @JsonProperty("numberOfReviewsRequired")
    private final Integer numberOfReviewsRequired;

    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public ProjectDto(@JsonProperty("id") String id,
                      @JsonProperty("name") String name,
                      @JsonProperty("description") String description,
                      @JsonProperty("contexts") List<ProjectContextDto> contexts,
                      @JsonProperty("numberOfReviewsRequired") Integer numberOfReviewsRequired,
                      @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.contexts = contexts;
        this.numberOfReviewsRequired = numberOfReviewsRequired;
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

    public List<ProjectContextDto> getContexts() {
        return contexts;
    }

    public Integer getNumberOfReviewsRequired() {
        return numberOfReviewsRequired;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}
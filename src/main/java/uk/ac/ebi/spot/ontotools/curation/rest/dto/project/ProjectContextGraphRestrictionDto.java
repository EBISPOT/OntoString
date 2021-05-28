package uk.ac.ebi.spot.ontotools.curation.rest.dto.project;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class ProjectContextGraphRestrictionDto implements Serializable {

    private static final long serialVersionUID = 1474260356785349687L;

    @NotEmpty
    @JsonProperty("classes")
    private final List<String> classes;

    @JsonProperty("iris")
    private final List<String> iris;

    @NotEmpty
    @JsonProperty("relations")
    private final List<String> relations;

    @NotEmpty
    @JsonProperty("direct")
    private final Boolean direct;

    @NotEmpty
    @JsonProperty("include_self")
    private final Boolean includeSelf;

    @JsonCreator
    public ProjectContextGraphRestrictionDto(@JsonProperty("classes") List<String> classes,
                                             @JsonProperty("iris") List<String> iris,
                                             @JsonProperty("relations") List<String> relations,
                                             @JsonProperty("direct") Boolean direct,
                                             @JsonProperty("include_self") Boolean includeSelf) {
        this.classes = classes;
        this.iris = iris;
        this.relations = relations;
        this.direct = direct;
        this.includeSelf = includeSelf;
    }

    public List<String> getClasses() {
        return classes;
    }

    public List<String> getIris() {
        return iris;
    }

    public List<String> getRelations() {
        return relations;
    }

    public Boolean getDirect() {
        return direct;
    }

    public Boolean getIncludeSelf() {
        return includeSelf;
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.EqualsAndHashCode;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProvenanceDto;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@EqualsAndHashCode
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class MappingDto implements Serializable {

    private static final long serialVersionUID = -2548737672325162378L;

    @NotNull
    @JsonProperty("id")
    private final String id;

    @NotNull
    @JsonProperty("entityId")
    private final String entityId;

    @NotNull
    @JsonProperty("ontologyTerm")
    private final OntologyTermDto ontologyTerm;

    @JsonProperty("reviewed")
    private final boolean reviewed;

    @JsonProperty("status")
    private final String status;

    @JsonProperty("reviews")
    private final List<ReviewDto> reviews;

    @JsonProperty("comments")
    private final List<CommentDto> comments;

    @NotNull
    @JsonProperty("created")
    private final ProvenanceDto created;

    @JsonCreator
    public MappingDto(@JsonProperty("id") String id,
                      @JsonProperty("entityId") String entityId,
                      @JsonProperty("ontologyTerm") OntologyTermDto ontologyTerm,
                      @JsonProperty("reviewed") boolean reviewed,
                      @JsonProperty("status") String status,
                      @JsonProperty("reviews") List<ReviewDto> reviews,
                      @JsonProperty("comments") List<CommentDto> comments,
                      @JsonProperty("created") ProvenanceDto created) {
        this.id = id;
        this.entityId = entityId;
        this.ontologyTerm = ontologyTerm;
        this.reviewed = reviewed;
        this.status = status;
        this.reviews = reviews;
        this.comments = comments;
        this.created = created;
    }

    public String getId() {
        return id;
    }

    public OntologyTermDto getOntologyTerm() {
        return ontologyTerm;
    }

    public String getEntityId() {
        return entityId;
    }

    public boolean isReviewed() {
        return reviewed;
    }

    public String getStatus() {
        return status;
    }

    public List<ReviewDto> getReviews() {
        return reviews;
    }

    public List<CommentDto> getComments() {
        return comments;
    }

    public ProvenanceDto getCreated() {
        return created;
    }
}

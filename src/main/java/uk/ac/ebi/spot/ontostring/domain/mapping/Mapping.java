package uk.ac.ebi.spot.ontostring.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.ac.ebi.spot.ontostring.constants.MappingStatus;
import uk.ac.ebi.spot.ontostring.domain.Provenance;
import uk.ac.ebi.spot.ontostring.domain.Review;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "mappings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({@CompoundIndex(name = "eoId", def = "{'entityId': 1, 'ontologyTermIds': 1}"),
        @CompoundIndex(name = "pco", def = "{'projectId': 1, 'context': 1, 'ontologyTermIds': 1}")})
public class Mapping {

    @Id
    private String id;

    @Indexed
    private String entityId;

    private String context;

    @Indexed
    private List<String> ontologyTermIds;

    @Indexed
    private String projectId;

    @Indexed
    private boolean reviewed;

    private List<Review> reviews;

    private List<Comment> comments;

    private String status;

    private Provenance created;

    @Transient
    private List<OntologyTerm> ontologyTerms;

    public void addReview(Review review, int noReviewsRequired) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        this.reviews.add(review);
        if (this.reviews.size() >= noReviewsRequired) {
            this.reviewed = true;
            this.status = MappingStatus.REQUIRED_REVIEWS_REACHED.name();
        }
    }
}

package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

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
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Review;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "mappings")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@CompoundIndexes({@CompoundIndex(name = "eoId", def = "{'entityId': 1, 'ontologyTermId': 1}")})
public class Mapping {

    @Id
    private String id;

    @Indexed
    private String entityId;

    @Indexed
    private String ontologyTermId;

    @Indexed
    private boolean reviewed;

    private List<Review> reviews;

    private String status;

    private Provenance created;

    @Transient
    private OntologyTerm ontologyTerm;

    public void addReview(Review review) {
        if (reviews == null) {
            reviews = new ArrayList<>();
        }
        this.reviews.add(review);
        if (this.reviews.size() >= CurationConstants.NO_REVIEWS_REQUIRED) {
            this.reviewed = true;
        }
    }
}

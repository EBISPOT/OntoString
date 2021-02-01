package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reviews")
@Getter
@Setter
public class Review {

    @Id
    private String id;

    private String reviewId;

    private Provenance created;

    public Review() {

    }

    public Review(String reviewId, Provenance created) {
        this.reviewId = reviewId;
        this.created = created;
    }
}

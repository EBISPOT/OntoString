package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Review;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ReviewDto;

public class ReviewDtoAssembler {

    public static ReviewDto assemble(Review review) {
        return new ReviewDto(review.getComment(), ProvenanceDtoAssembler.assemble(review.getCreated()));
    }
}

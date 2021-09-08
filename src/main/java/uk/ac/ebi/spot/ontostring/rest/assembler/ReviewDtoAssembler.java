package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.Review;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.ReviewDto;

public class ReviewDtoAssembler {

    public static ReviewDto assemble(Review review) {
        return new ReviewDto(review.getComment(), ProvenanceDtoAssembler.assemble(review.getCreated()));
    }
}

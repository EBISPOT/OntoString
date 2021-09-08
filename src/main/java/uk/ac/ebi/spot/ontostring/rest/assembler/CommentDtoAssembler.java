package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.mapping.Comment;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.CommentDto;

public class CommentDtoAssembler {

    public static CommentDto assemble(Comment comment) {
        return new CommentDto(comment.getBody(), ProvenanceDtoAssembler.assemble(comment.getCreated()));
    }
}

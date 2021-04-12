package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Comment;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.CommentDto;

public class CommentDtoAssembler {

    public static CommentDto assemble(Comment comment) {
        return new CommentDto(comment.getBody(), ProvenanceDtoAssembler.assemble(comment.getCreated()));
    }
}

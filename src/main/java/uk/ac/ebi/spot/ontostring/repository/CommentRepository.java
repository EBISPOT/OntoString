package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.mapping.Comment;

public interface CommentRepository extends MongoRepository<Comment, String> {
}

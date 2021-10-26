package uk.ac.ebi.spot.ontostring.domain.auth;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokens")
@Getter
@Setter
@AllArgsConstructor
public class AuthToken {

    @Id
    private String id;

    @Indexed
    private String token;

    private String email;
}

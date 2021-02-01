package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "users")
@Getter
@Setter
public class User {

    @Id
    private String id;

    private String username;

    private String email;

    private String role;

    public User() {

    }

    public User(String username, String email, String role) {
        this.username = username;
        this.email = email;
        this.role = role;
    }
}

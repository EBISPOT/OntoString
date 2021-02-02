package uk.ac.ebi.spot.ontotools.curation.domain.auth;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "projects")
@NoArgsConstructor
@Getter
@Setter
public class Project {

    @Id
    private String id;

    private String name;

    private String description;

}

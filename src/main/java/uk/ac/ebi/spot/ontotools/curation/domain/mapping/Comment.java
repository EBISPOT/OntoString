package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

@Document(collection = "comments")
@Getter
@Setter
public class Comment {

    @Id
    private String id;

    private String mappedTraitId;

    private Provenance created;

    private String body;

    public Comment() {

    }

    public Comment(String mappedTraitId, String body, Provenance created) {
        this.mappedTraitId = mappedTraitId;
        this.created = created;
        this.body = body;
    }
}

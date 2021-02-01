package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mappingSuggestions")
@Getter
@Setter
public class MappingSuggestion {

    @Id
    private String id;

    private String mappedTraitId;

    private String mappedTermId;

    private Provenance created;

    public MappingSuggestion() {

    }

    public MappingSuggestion(String mappedTraitId, String mappedTermId, Provenance created) {
        this.mappedTraitId = mappedTraitId;
        this.mappedTermId = mappedTermId;
        this.created = created;
    }
}

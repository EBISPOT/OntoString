package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mappings")
@Getter
@Setter
public class Mapping {

    @Id
    private String id;

    private String mappedTraitId;

    private String mappedTermId;

    private Provenance created;

    public Mapping() {

    }

    public Mapping(String mappedTraitId, String mappedTermId, Provenance created) {
        this.mappedTraitId = mappedTraitId;
        this.mappedTermId = mappedTermId;
        this.created = created;
    }
}

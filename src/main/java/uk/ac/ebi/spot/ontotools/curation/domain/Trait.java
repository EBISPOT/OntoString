package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "traits")
@Getter
@Setter
public class Trait {

    @Id
    private String id;

    private String name;

    private String currentMappingId;

    private int noSourceRecords;

    private Provenance created;

    private Provenance lastUpdated;

    public Trait() {
    }


    public Trait(String name, String currentMappingId, Provenance created) {
        this.name = name;
        this.currentMappingId = currentMappingId;
        this.noSourceRecords = 0;
        this.created = created;
        this.lastUpdated = created;
    }
}

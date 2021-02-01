package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import org.joda.time.DateTime;

@Getter
public class Provenance {

    private String userId;

    private DateTime timestamp;

    public Provenance(String userId, DateTime timestamp) {
        this.userId = userId;
        this.timestamp = timestamp;
    }
}

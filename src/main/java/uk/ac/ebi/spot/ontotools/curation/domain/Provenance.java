package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.DateTime;

@Getter
@AllArgsConstructor
public class Provenance {

    private String userName;

    private String userEmail;

    private DateTime timestamp;
}

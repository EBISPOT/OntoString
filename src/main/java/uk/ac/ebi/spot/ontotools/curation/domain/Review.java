package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class Review {

    private String comment;

    private Provenance created;

}

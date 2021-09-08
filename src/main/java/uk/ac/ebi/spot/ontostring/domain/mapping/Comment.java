package uk.ac.ebi.spot.ontostring.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.ac.ebi.spot.ontostring.domain.Provenance;

@Getter
@AllArgsConstructor
public class Comment {

    private String body;

    private Provenance created;

}

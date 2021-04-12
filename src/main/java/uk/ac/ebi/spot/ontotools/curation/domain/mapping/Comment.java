package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

@Getter
@AllArgsConstructor
public class Comment {

    private String body;

    private Provenance created;

}

package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class OntologyTermContext {

    private String projectId;

    private String context;

    private String status;

}

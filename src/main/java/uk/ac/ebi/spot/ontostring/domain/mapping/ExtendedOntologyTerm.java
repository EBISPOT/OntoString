package uk.ac.ebi.spot.ontostring.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Map;

@AllArgsConstructor
@Getter
public class ExtendedOntologyTerm {

    private Map<String, String> entities;

    private OntologyTerm ontologyTerm;
}

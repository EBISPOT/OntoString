package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.OntologyTermDto;

public class OntologyTermDtoAssembler {

    public static OntologyTermDto assemble(OntologyTerm ontologyTerm) {
        return new OntologyTermDto(ontologyTerm.getCurie(),
                ontologyTerm.getIri(),
                ontologyTerm.getLabel(),
                ontologyTerm.getStatus(),
                ontologyTerm.getDescription(),
                ontologyTerm.getCrossRefs());
    }
}

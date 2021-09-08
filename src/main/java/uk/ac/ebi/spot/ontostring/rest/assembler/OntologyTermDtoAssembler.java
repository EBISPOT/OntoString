package uk.ac.ebi.spot.ontostring.rest.assembler;

import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.OntologyTermDto;

import java.util.ArrayList;

public class OntologyTermDtoAssembler {

    public static OntologyTermDto assemble(OntologyTerm ontologyTerm) {
        return new OntologyTermDto(ontologyTerm.getCurie(),
                ontologyTerm.getIri(),
                ontologyTerm.getLabel(),
                ontologyTerm.getStatus(),
                ontologyTerm.getDescription(),
                ontologyTerm.getCrossRefs());
    }

    public static OntologyTerm disassemble(OntologyTermDto ontologyTerm) {
        return new OntologyTerm(null, ontologyTerm.getCurie(),
                ontologyTerm.getIri(),
                DigestUtils.sha256Hex(ontologyTerm.getIri()),
                ontologyTerm.getLabel(),
                ontologyTerm.getDescription(),
                ontologyTerm.getCrossRefs(),
                new ArrayList<>(), null);
    }
}

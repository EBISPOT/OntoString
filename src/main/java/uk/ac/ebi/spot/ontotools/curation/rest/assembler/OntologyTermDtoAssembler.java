package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import org.apache.commons.codec.digest.DigestUtils;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;

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
        return new OntologyTerm(null,
                ontologyTerm.getCurie(),
                ontologyTerm.getIri(),
                DigestUtils.sha256Hex(ontologyTerm.getIri()),
                ontologyTerm.getLabel(),
                ontologyTerm.getStatus() != null ? ontologyTerm.getStatus() : TermStatus.NEEDS_IMPORT.name(),
                ontologyTerm.getDescription(),
                ontologyTerm.getCrossRefs());
    }
}

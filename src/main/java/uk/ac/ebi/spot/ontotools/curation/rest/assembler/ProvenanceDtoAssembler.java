package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProvenanceDto;

public class ProvenanceDtoAssembler {

    public static ProvenanceDto assemble(Provenance provenance) {
        return new ProvenanceDto(UserDtoAssembler.assemble(provenance.getUserName(), provenance.getUserEmail()), provenance.getTimestamp());
    }

    public static Provenance disassemble(ProvenanceDto provenance) {
        return new Provenance(provenance.getUser().getName(), provenance.getUser().getEmail(), provenance.getTimestamp());
    }
}

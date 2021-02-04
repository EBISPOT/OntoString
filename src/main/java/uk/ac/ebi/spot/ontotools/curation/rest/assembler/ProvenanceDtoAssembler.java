package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProvenanceDto;

public class ProvenanceDtoAssembler {

    public static ProvenanceDto assemble(Provenance provenance, User user) {
        return new ProvenanceDto(UserDtoAssembler.asseble(user), provenance.getTimestamp());
    }

    public static Provenance disassemble(ProvenanceDto provenance) {
        return new Provenance(provenance.getUser().getId(), provenance.getTimestamp());
    }
}

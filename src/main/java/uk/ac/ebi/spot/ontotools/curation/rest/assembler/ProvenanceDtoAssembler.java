package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ProvenanceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.UserDto;

public class ProvenanceDtoAssembler {

    public static ProvenanceDto assemble(Provenance provenance) {
        return new ProvenanceDto(new UserDto(provenance.getUserId(), null, null), provenance.getTimestamp());
    }

}

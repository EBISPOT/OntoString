package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.MappingDto;

public class MappingDtoAssembler {

    public static MappingDto assemble(Mapping mapping, OntologyTerm ontologyTerm) {
        return new MappingDto(mapping.getId(),
                OntologyTermDtoAssembler.assemble(ontologyTerm),
                ProvenanceDtoAssembler.assemble(mapping.getCreated()));
    }

}

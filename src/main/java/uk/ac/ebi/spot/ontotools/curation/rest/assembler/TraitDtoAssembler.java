package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Trait;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.TraitDto;

public class TraitDtoAssembler {

    public static TraitDto assemble(Trait trait, Mapping mapping, OntologyTerm ontologyTerm) {
        return new TraitDto(trait.getId(),
                trait.getName(),
                MappingDtoAssembler.assemble(mapping, ontologyTerm),
                trait.getNoSourceRecords(),
                ProvenanceDtoAssembler.assemble(trait.getCreated()),
                ProvenanceDtoAssembler.assemble(trait.getLastUpdated()));
    }
}

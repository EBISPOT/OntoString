package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.MappingSuggestionDto;

public class MappingSuggestionDtoAssembler {

    public static MappingSuggestionDto assemble(MappingSuggestion mappingSuggestion) {
        return new MappingSuggestionDto(mappingSuggestion.getId(),
                mappingSuggestion.getEntityId(),
                OntologyTermDtoAssembler.assemble(mappingSuggestion.getOntologyTerm()),
                ProvenanceDtoAssembler.assemble(mappingSuggestion.getCreated()));
    }
}

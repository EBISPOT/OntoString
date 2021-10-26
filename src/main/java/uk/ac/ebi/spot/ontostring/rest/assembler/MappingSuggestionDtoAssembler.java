package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.MappingSuggestionDto;

public class MappingSuggestionDtoAssembler {

    public static MappingSuggestionDto assemble(MappingSuggestion mappingSuggestion) {
        return new MappingSuggestionDto(mappingSuggestion.getId(),
                mappingSuggestion.getEntityId(),
                OntologyTermDtoAssembler.assemble(mappingSuggestion.getOntologyTerm()),
                ProvenanceDtoAssembler.assemble(mappingSuggestion.getCreated()));
    }
}

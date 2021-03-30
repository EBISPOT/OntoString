package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;

public class MappingSuggestionDtoAssembler {

    public static MappingSuggestionDto assemble(MappingSuggestion mappingSuggestion, String projectId, String context) {
        return new MappingSuggestionDto(mappingSuggestion.getId(),
                mappingSuggestion.getEntityId(),
                OntologyTermDtoAssembler.assemble(mappingSuggestion.getOntologyTerm(), projectId, context),
                ProvenanceDtoAssembler.assemble(mappingSuggestion.getCreated()));
    }
}

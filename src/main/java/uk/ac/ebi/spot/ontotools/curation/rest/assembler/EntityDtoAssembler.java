package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;

import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoAssembler {

    public static EntityDto assemble(Entity entity, SourceDto source, List<Mapping> mappings, List<MappingSuggestion> mappingSuggestions) {
        List<MappingSuggestionDto> mappingSuggestionDtos = mappingSuggestions.stream().map(MappingSuggestionDtoAssembler::assemble).collect(Collectors.toList());
        List<MappingDto> mappingDtos = mappings.stream().map(MappingDtoAssembler::assemble).collect(Collectors.toList());

        return new EntityDto(entity.getId(),
                source,
                entity.getName(),
                entity.getBaseId(),
                entity.getBaseField(),
                entity.getMappingStatus().name(),
                mappingSuggestionDtos,
                mappingDtos,
                ProvenanceDtoAssembler.assemble(entity.getCreated()));
    }
}

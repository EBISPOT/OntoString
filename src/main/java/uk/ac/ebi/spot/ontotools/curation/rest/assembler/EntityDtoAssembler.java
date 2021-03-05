package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoAssembler {

    public static EntityDto assemble(Entity entity, SourceDto source, Mapping mapping, List<MappingSuggestion> mappingSuggestions) {
        List<MappingSuggestionDto> mappingSuggestionDtos = mappingSuggestions != null ? mappingSuggestions.stream().map(MappingSuggestionDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>();
        MappingDto mappingDto = mapping != null ? MappingDtoAssembler.assemble(mapping) : null;

        return new EntityDto(entity.getId(),
                source,
                entity.getName(),
                entity.getBaseId(),
                entity.getBaseField(),
                entity.getMappingStatus().name(),
                mappingSuggestionDtos,
                mappingDto,
                ProvenanceDtoAssembler.assemble(entity.getCreated()));
    }
}

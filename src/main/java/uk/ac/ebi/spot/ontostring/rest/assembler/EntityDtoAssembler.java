package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.AuditEntry;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontostring.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontostring.rest.dto.project.SourceDto;
import uk.ac.ebi.spot.ontostring.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontostring.rest.dto.audit.AuditEntryDto;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontostring.rest.dto.mapping.MappingSuggestionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoAssembler {

    public static EntityDto assemble(Entity entity, SourceDto source, Mapping mapping, List<MappingSuggestion> mappingSuggestions, List<AuditEntry> auditEntries) {
        List<MappingSuggestionDto> mappingSuggestionDtos = new ArrayList<>();
        if (mappingSuggestions != null) {
            for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
                mappingSuggestionDtos.add(MappingSuggestionDtoAssembler.assemble(mappingSuggestion));
            }
        }
        MappingDto mappingDto = mapping != null ? MappingDtoAssembler.assemble(mapping) : null;
        List<AuditEntryDto> auditTrail = auditEntries != null ? auditEntries.stream().map(AuditEntryDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>();

        return new EntityDto(entity.getId(),
                source,
                entity.getName(),
                entity.getBaseId(),
                entity.getContext(),
                entity.getPriority(),
                entity.getMappingStatus().name(),
                mappingSuggestionDtos,
                mappingDto,
                auditTrail,
                ProvenanceDtoAssembler.assemble(entity.getCreated()));
    }
}

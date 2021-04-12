package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.AuditEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.EntityDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.AuditEntryDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingSuggestionDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EntityDtoAssembler {

    public static EntityDto assemble(Entity entity, SourceDto source, Mapping mapping, List<MappingSuggestion> mappingSuggestions, List<AuditEntry> auditEntries) {
        List<MappingSuggestionDto> mappingSuggestionDtos = new ArrayList<>();
        if (mappingSuggestions != null) {
            for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
                mappingSuggestionDtos.add(MappingSuggestionDtoAssembler.assemble(mappingSuggestion, entity.getProjectId(), entity.getContext()));
            }
        }
        MappingDto mappingDto = null;
        if (mapping != null) {
            mappingDto = MappingDtoAssembler.assemble(mapping, entity.getProjectId(), entity.getContext());
        }
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

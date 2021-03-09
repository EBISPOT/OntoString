package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.AuditEntry;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.AuditEntryDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.AuditUserDto;

import java.util.stream.Collectors;

public class AuditEntryDtoAssembler {

    public static AuditEntryDto assemble(AuditEntry auditEntry) {
        return new AuditEntryDto(auditEntry.getAction(),
                new AuditUserDto(auditEntry.getProvenance().getUserName(), auditEntry.getProvenance().getUserEmail()),
                auditEntry.getMetadataEntries().stream().map(MetadataEntryDtoAssembler::assemble).collect(Collectors.toList()),
                auditEntry.getTimestamp());
    }
}

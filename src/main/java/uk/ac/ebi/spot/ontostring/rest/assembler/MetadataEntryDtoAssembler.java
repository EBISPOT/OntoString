package uk.ac.ebi.spot.ontostring.rest.assembler;

import uk.ac.ebi.spot.ontostring.domain.MetadataEntry;
import uk.ac.ebi.spot.ontostring.rest.dto.audit.MetadataEntryDto;

public class MetadataEntryDtoAssembler {
    public static MetadataEntryDto assemble(MetadataEntry metadataEntry) {
        return new MetadataEntryDto(metadataEntry.getKey(), metadataEntry.getValue(), metadataEntry.getAction());
    }
}

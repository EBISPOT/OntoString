package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.MetadataEntry;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.audit.MetadataEntryDto;

public class MetadataEntryDtoAssembler {
    public static MetadataEntryDto assemble(MetadataEntry metadataEntry) {
        return new MetadataEntryDto(metadataEntry.getKey(), metadataEntry.getValue());
    }
}

package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;

public class SourceDtoAssembler {

    public static SourceDto assemble(Source source) {
        return new SourceDto(source.getId(),
                source.getName(),
                source.getDescription(),
                source.getUri(),
                source.getType(),
                ProvenanceDtoAssembler.assemble(source.getCreated()),
                ProvenanceDtoAssembler.assemble(source.getLastUpdated()));
    }

    public static Source disassemble(SourceCreationDto source, Provenance provenance) {
        return new Source(null,
                source.getName(),
                source.getDescription(),
                source.getUri(),
                source.getType(),
                provenance,
                provenance,
                null,
                null,
                false);
    }
}

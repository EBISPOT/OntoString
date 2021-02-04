package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceCreationDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.SourceDto;

public class SourceDtoAssembler {

    public static SourceDto assemble(Source source, User user) {
        return new SourceDto(source.getId(),
                source.getName(),
                source.getDescription(),
                source.getUri(),
                source.getType(),
                ProvenanceDtoAssembler.assemble(source.getCreated(), user),
                ProvenanceDtoAssembler.assemble(source.getLastUpdated(), user));
    }

    public static Source disassemble(SourceDto source) {
        return new Source(source.getId(),
                source.getName(),
                source.getDescription(),
                source.getUri(),
                source.getType(),
                ProvenanceDtoAssembler.disassemble(source.getCreated()),
                ProvenanceDtoAssembler.disassemble(source.getLastUpdated()),
                null,
                null,
                false);
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

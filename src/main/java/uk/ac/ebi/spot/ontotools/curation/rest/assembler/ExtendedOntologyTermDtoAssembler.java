package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.mapping.ExtendedOntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ExtendedOntologyTermDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.ShortEntityInfoDto;

import java.util.ArrayList;
import java.util.List;

public class ExtendedOntologyTermDtoAssembler {

    public static ExtendedOntologyTermDto assemble(ExtendedOntologyTerm extendedOntologyTerm, String projectId, String context) {
        List<ShortEntityInfoDto> entities = new ArrayList<>();
        for (String id : extendedOntologyTerm.getEntities().keySet()) {
            entities.add(new ShortEntityInfoDto(id, extendedOntologyTerm.getEntities().get(id)));
        }

        return new ExtendedOntologyTermDto(entities,
                OntologyTermDtoAssembler.assemble(extendedOntologyTerm.getOntologyTerm(), projectId, context));
    }

}

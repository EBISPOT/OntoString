package uk.ac.ebi.spot.ontotools.curation.rest.assembler;

import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.MappingDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.mapping.OntologyTermDto;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MappingDtoAssembler {

    public static MappingDto assemble(Mapping mapping) {
        List<OntologyTermDto> ontologyTermDtos = new ArrayList<>();
        for (OntologyTerm ontologyTerm : mapping.getOntologyTerms()) {
            ontologyTermDtos.add(OntologyTermDtoAssembler.assemble(ontologyTerm));
        }

        return new MappingDto(mapping.getId(),
                mapping.getEntityId(),
                ontologyTermDtos,
                mapping.isReviewed(),
                mapping.getStatus(),
                mapping.getReviews() != null ? mapping.getReviews().stream().map(ReviewDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>(),
                mapping.getComments() != null ? mapping.getComments().stream().map(CommentDtoAssembler::assemble).collect(Collectors.toList()) : new ArrayList<>(),
                ProvenanceDtoAssembler.assemble(mapping.getCreated()));
    }
}

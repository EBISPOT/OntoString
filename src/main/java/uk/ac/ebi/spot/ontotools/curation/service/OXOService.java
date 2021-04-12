package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXOMappingResponseDto;

import java.util.List;

public interface OXOService {

    List<OXOMappingResponseDto> findMapping(List<String> ids, List<String> ontologies);
}

package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.rest.dto.oxo.OXOMappingResponseDto;

import java.util.List;

public interface OXOService {

    List<OXOMappingResponseDto> findMapping(List<String> ids, List<String> ontologies);
}

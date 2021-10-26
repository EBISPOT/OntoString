package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.rest.dto.zooma.ZoomaResponseDto;

import java.util.List;

public interface ZoomaService {

    List<ZoomaResponseDto> annotate(String entityValue, List<String> datasources, List<String> ontologies);
}

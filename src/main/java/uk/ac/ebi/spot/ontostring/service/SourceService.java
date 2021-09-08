package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.Source;

import java.util.List;

public interface SourceService {
    Source createSource(Source source, String projectId);

    List<Source> getSources(String projectId);

    Source getSource(String sourceId, String projectId);
}

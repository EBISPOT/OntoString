package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Source;

import java.util.List;

public interface SourceService {
    Source createSource(Source source, String projectId);

    List<Source> getSources(String projectId);

    Source getSource(String sourceId, String projectId);
}

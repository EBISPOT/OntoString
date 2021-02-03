package uk.ac.ebi.spot.ontotools.curation.service;

import java.util.List;
import java.util.Map;

public interface ZoomaService {

    Map<String, List<String>> annotate(String entityValue, List<String> datasources, List<String> ontologies);
}

package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Map;

public interface MappingService {

    Mapping createMapping(Entity entity, List<OntologyTerm> ontologyTerms, Provenance provenance);

    Map<String, Mapping> retrieveMappingsForEntities(List<String> entityIds);

    Mapping retrieveMappingForEntity(String entityId);

    Mapping addReviewToMapping(String mappingId, String comment, int noReviewsRequired, Provenance provenance);

    Mapping retrieveMappingById(String mappingId);

    Mapping addCommentToMapping(String mappingId, String body, Provenance provenance);

    Mapping updateMapping(String mappingId, List<OntologyTerm> ontologyTerms, Provenance provenance);

    void deleteMapping(String mappingId, Provenance provenance, Map<String, String> metadata);
}

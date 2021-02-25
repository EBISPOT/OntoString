package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Map;

public interface MappingService {

    Mapping createMapping(Entity entity, OntologyTerm ontologyTerm, Provenance provenance);

    Map<String, List<Mapping>> retrieveMappingsForEntities(List<String> entityIds);

    List<Mapping> retrieveMappingsForEntity(String entityId);

    List<String> deleteMappingExcluding(Entity entity, String ontologyTermId);

    Mapping addReviewToMapping(String mappingId, String comment, int noReviewsRequired, Provenance provenance);

    Mapping retrieveMappingById(String mappingId);

    Mapping addCommentToMapping(String mappingId, String body, Provenance provenance);
}

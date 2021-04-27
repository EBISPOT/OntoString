package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.MetadataEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.List;
import java.util.Map;

public interface MappingService {

    Mapping createMapping(Entity entity, List<OntologyTerm> ontologyTerms, Provenance provenance);

    Map<String, Mapping> retrieveMappingsForEntities(List<String> entityIds, String projectId, String context);

    Mapping retrieveMappingForEntity(String entityId);

    Mapping addReviewToMapping(String mappingId, String comment, int noReviewsRequired, Provenance provenance);

    Mapping retrieveMappingById(String mappingId);

    Mapping addCommentToMapping(String mappingId, String body, Provenance provenance);

    Mapping updateMapping(String mappingId, List<OntologyTerm> newTerms, List<OntologyTerm> oldTerms, Provenance provenance);

    void deleteMapping(String mappingId, Provenance provenance, List<MetadataEntry> metadata);

    void updateStatusForObsoleteMappings(String ontologyTermId, String projectId, String context);
}

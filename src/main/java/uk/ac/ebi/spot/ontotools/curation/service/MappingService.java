package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

import java.util.List;
import java.util.Map;

public interface MappingService {

    Mapping createMapping(Entity entity, OntologyTerm ontologyTerm, Provenance provenance);

    Map<String, List<Mapping>> retrieveMappingsForEntities(List<String> entityIds);
}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MappingServiceImpl implements MappingService {

    private static final Logger log = LoggerFactory.getLogger(MappingService.class);

    @Autowired
    private MappingRepository mappingRepository;

    @Autowired
    private OntologyTermService ontologyTermService;

    @Override
    public Mapping createMapping(Entity entity, OntologyTerm ontologyTerm, Provenance provenance) {
        log.info("Creating mapping for entity [{}]: {}", entity.getName(), ontologyTerm.getCurie());
        Optional<Mapping> mappingOp = mappingRepository.findByEntityIdAndOntologyTermId(entity.getId(), ontologyTerm.getId());
        if (mappingOp.isPresent()) {
            log.warn("Mapping for between entity [{}] and ontology term [{}] already exists: {}", entity.getName(), ontologyTerm.getCurie(), mappingOp.get().getId());
            return mappingOp.get();
        }

        Mapping created = mappingRepository.insert(new Mapping(null, entity.getId(), ontologyTerm.getId(), false, new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(), provenance, null));
        log.info("Mapping for between entity [{}] and ontology term [{}] created: {}", entity.getName(), ontologyTerm.getCurie(), created.getId());
        return created;
    }

    @Override
    public Map<String, List<Mapping>> retrieveMappingsForEntities(List<String> entityIds) {
        log.info("Retrieving mappings for entities: {}", entityIds);
        List<Mapping> mappings = mappingRepository.findByEntityIdIn(entityIds);
        List<String> ontologyTermIds = mappings.stream().map(Mapping::getOntologyTermId).collect(Collectors.toList());
        Map<String, OntologyTerm> ontologyTermMap = ontologyTermService.retrieveTerms(ontologyTermIds);
        log.info("Found {} mappings.", mappings.size());
        Map<String, List<Mapping>> result = new HashMap<>();
        for (Mapping mapping : mappings) {
            if (!ontologyTermMap.containsKey(mapping.getOntologyTermId())) {
                log.warn("Unable to find ontology term [{}] for mapping suggestion: {}", mapping.getOntologyTermId(), mapping.getId());
                continue;
            }
            List<Mapping> list = result.containsKey(mapping.getEntityId()) ? result.get(mapping.getEntityId()) : new ArrayList<>();
            mapping.setOntologyTerm(ontologyTermMap.get(mapping.getOntologyTermId()));
            list.add(mapping);
            result.put(mapping.getEntityId(), list);
        }
        return result;
    }
}

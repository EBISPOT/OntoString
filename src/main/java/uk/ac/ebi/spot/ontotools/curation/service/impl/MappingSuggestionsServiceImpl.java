package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.MappingSuggestion;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingSuggestionRepository;
import uk.ac.ebi.spot.ontotools.curation.service.MappingSuggestionsService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class MappingSuggestionsServiceImpl implements MappingSuggestionsService {

    private static final Logger log = LoggerFactory.getLogger(MappingSuggestionsService.class);

    @Autowired
    private MappingSuggestionRepository mappingSuggestionRepository;

    @Autowired
    private OntologyTermService ontologyTermService;

    @Override
    public MappingSuggestion createMappingSuggestion(Entity entity, OntologyTerm ontologyTerm, Provenance provenance) {
        log.info("Creating mapping suggestion for entity [{}]: {}", entity.getName(), ontologyTerm.getCurie());
        Optional<MappingSuggestion> mappingSuggestionOp = mappingSuggestionRepository.findByEntityIdAndOntologyTermId(entity.getId(), ontologyTerm.getId());
        if (mappingSuggestionOp.isPresent()) {
            log.warn("Mapping suggestion already exists: {}", mappingSuggestionOp.get().getId());
            return mappingSuggestionOp.get();
        }

        MappingSuggestion created = mappingSuggestionRepository.insert(new MappingSuggestion(null, entity.getId(), ontologyTerm.getId(), provenance, null));
        log.info("[{} | {}] Mapping suggestion created: {}", entity.getName(), ontologyTerm.getCurie(), created.getId());
        return created;
    }

    @Override
    @Async
    public void deleteMappingSuggestionsExcluding(Entity entity, List<OntologyTerm> ontologyTerms) {
        log.info("Deleting [{}] old mapping suggestions for: {}", ontologyTerms.size(), entity.getName());
        List<String> ontologyTermIds = ontologyTerms.stream().map(OntologyTerm::getId).collect(Collectors.toList());
        List<MappingSuggestion> toDelete = mappingSuggestionRepository.findByEntityIdAndOntologyTermIdNotIn(entity.getId(), ontologyTermIds);
        log.info("[{}] Found {} mapping suggestions to delete.", entity.getName(), toDelete.size());
        mappingSuggestionRepository.deleteAll(toDelete);
    }

    @Override
    public Map<String, List<MappingSuggestion>> retrieveMappingSuggestionsForEntities(List<String> entityIds) {
        log.info("Retrieving mapping suggestions for entities: {}", entityIds);
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionRepository.findByEntityIdIn(entityIds);
        List<String> ontologyTermIds = mappingSuggestions.stream().map(MappingSuggestion::getOntologyTermId).collect(Collectors.toList());
        Map<String, OntologyTerm> ontologyTermMap = ontologyTermService.retrieveTerms(ontologyTermIds);
        log.info("Found {} mapping suggestions.", mappingSuggestions.size());
        Map<String, List<MappingSuggestion>> result = new HashMap<>();
        for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
            if (!ontologyTermMap.containsKey(mappingSuggestion.getOntologyTermId())) {
                log.warn("Unable to find ontology term [{}] for mapping suggestion: {}", mappingSuggestion.getOntologyTermId(), mappingSuggestion.getId());
                continue;
            }
            List<MappingSuggestion> list = result.containsKey(mappingSuggestion.getEntityId()) ? result.get(mappingSuggestion.getEntityId()) : new ArrayList<>();
            mappingSuggestion.setOntologyTerm(ontologyTermMap.get(mappingSuggestion.getOntologyTermId()));
            list.add(mappingSuggestion);
            result.put(mappingSuggestion.getEntityId(), list);
        }
        return result;
    }

    @Override
    public List<MappingSuggestion> retrieveMappingSuggestionsForEntity(String entityId) {
        log.info("Retrieving mapping suggestions for entity: {}", entityId);
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionRepository.findByEntityId(entityId);
        List<String> ontologyTermIds = mappingSuggestions.stream().map(MappingSuggestion::getOntologyTermId).collect(Collectors.toList());
        Map<String, OntologyTerm> ontologyTermMap = ontologyTermService.retrieveTerms(ontologyTermIds);
        log.info("Found {} mapping suggestions.", mappingSuggestions.size());
        List<MappingSuggestion> result = new ArrayList<>();
        for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
            if (!ontologyTermMap.containsKey(mappingSuggestion.getOntologyTermId())) {
                log.warn("Unable to find ontology term [{}] for mapping suggestion: {}", mappingSuggestion.getOntologyTermId(), mappingSuggestion.getId());
                continue;
            }
            mappingSuggestion.setOntologyTerm(ontologyTermMap.get(mappingSuggestion.getOntologyTermId()));
            result.add(mappingSuggestion);
        }
        return result;
    }

    @Override
    public void deleteMappingSuggestions(String entityId, String ontologyTermId) {
        log.info("Deleting mapping suggestions for entity [{}] with ontology term: {}", entityId, ontologyTermId);
        List<MappingSuggestion> mappingSuggestions = mappingSuggestionRepository.findByEntityId(entityId);
        for (MappingSuggestion mappingSuggestion : mappingSuggestions) {
            if (!mappingSuggestion.getOntologyTermId().equalsIgnoreCase(ontologyTermId)) {
                mappingSuggestionRepository.delete(mappingSuggestion);
            }
        }
    }
}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Review;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Comment;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;

import java.util.*;

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
        List<Mapping> mappings = mappingRepository.findByEntityId(entity.getId());
        for (Mapping mapping : mappings) {
            if (mapping.getOntologyTermIds().contains(ontologyTerm.getId())) {
                log.warn("Mapping for between entity [{}] and ontology term [{}] already exists: {}", entity.getName(), ontologyTerm.getCurie(), mapping.getId());
                return mapping;
            }
        }

        Mapping created = mappingRepository.insert(new Mapping(null, entity.getId(), Arrays.asList(new String[]{ontologyTerm.getId()}), entity.getProjectId(),
                false, new ArrayList<>(), new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(), provenance, null));
        log.info("Mapping for between entity [{}] and ontology term [{}] created: {}", entity.getName(), ontologyTerm.getCurie(), created.getId());
        return created;
    }

    @Override
    public void updateMapping(String mappingId, OntologyTerm ontologyTerm, Provenance provenance) {
        log.info("Updating mapping [{}]: {}", mappingId, ontologyTerm.getCurie());
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }

        /**
         * TODO: Associate the provenance to the ontology term rather than the entire mapping !?
         */
        Mapping mapping = mappingOp.get();
        mapping.getOntologyTermIds().add(ontologyTerm.getId());
        mappingRepository.save(mapping);
    }

    @Override
    public List<String> deleteMapping(String mappingId, String ontoTermId, Provenance provenance) {
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }

        Mapping mapping = mappingOp.get();
        List<String> result = new ArrayList<>();
        if (ontoTermId != null) {
            mapping.getOntologyTermIds().remove(ontoTermId);
            result.add(ontoTermId);
            mappingRepository.save(mapping);
        } else {
            result.addAll(mapping.getOntologyTermIds());
            mappingRepository.delete(mapping);
        }

        return result;
    }

    @Override
    public Map<String, List<Mapping>> retrieveMappingsForEntities(List<String> entityIds) {
        log.info("Retrieving mappings for entities: {}", entityIds);
        List<Mapping> mappings = mappingRepository.findByEntityIdIn(entityIds);
        List<String> ontologyTermIds = new ArrayList<>();
        for (Mapping mapping : mappings) {
            for (String oId : mapping.getOntologyTermIds()) {
                if (!ontologyTermIds.contains(oId)) {
                    ontologyTermIds.add(oId);
                }
            }
        }
        Map<String, OntologyTerm> ontologyTermMap = ontologyTermService.retrieveTerms(ontologyTermIds);
        log.info("Found {} mappings.", mappings.size());
        Map<String, List<Mapping>> result = new HashMap<>();
        for (Mapping mapping : mappings) {
            List<OntologyTerm> ontologyTerms = new ArrayList<>();
            for (String oId : mapping.getOntologyTermIds()) {
                if (!ontologyTermMap.containsKey(oId)) {
                    log.warn("Unable to find ontology term [{}] for mapping suggestion: {}", oId, mapping.getId());
                    continue;
                } else {
                    ontologyTerms.add(ontologyTermMap.get(oId));
                }
            }
            List<Mapping> list = result.containsKey(mapping.getEntityId()) ? result.get(mapping.getEntityId()) : new ArrayList<>();
            mapping.setOntologyTerms(ontologyTerms);
            list.add(mapping);
            result.put(mapping.getEntityId(), list);
        }
        return result;
    }

    @Override
    public List<Mapping> retrieveMappingsForEntity(String entityId) {
        log.info("Retrieving mappings for entity: {}", entityId);
        List<Mapping> mappings = mappingRepository.findByEntityId(entityId);
        List<String> ontologyTermIds = new ArrayList<>();
        for (Mapping mapping : mappings) {
            for (String oId : mapping.getOntologyTermIds()) {
                if (!ontologyTermIds.contains(oId)) {
                    ontologyTermIds.add(oId);
                }
            }
        }

        Map<String, OntologyTerm> ontologyTermMap = ontologyTermService.retrieveTerms(ontologyTermIds);
        log.info("Found {} mappings.", mappings.size());
        List<Mapping> result = new ArrayList<>();
        for (Mapping mapping : mappings) {
            List<OntologyTerm> ontologyTerms = new ArrayList<>();
            for (String oId : mapping.getOntologyTermIds()) {
                if (!ontologyTermMap.containsKey(oId)) {
                    log.warn("Unable to find ontology term [{}] for mapping suggestion: {}", oId, mapping.getId());
                    continue;
                } else {
                    ontologyTerms.add(ontologyTermMap.get(oId));
                }
            }
            mapping.setOntologyTerms(ontologyTerms);
            result.add(mapping);
        }
        return result;
    }


    /*
    @Override
    public List<String> deleteMappingExcluding(Entity entity, String ontologyTermId) {
        log.info("Deleting mappings for entity [{}] excluding ontology term: {}", entity.getId(), ontologyTermId);
        List<Mapping> mappings = mappingRepository.findByEntityId(entity.getId());
        List<String> result = new ArrayList<>();
        for (Mapping mapping : mappings) {
            if (!mapping.getOntologyTermIds().contains(ontologyTermId)) {
                result.add(mapping.getOntologyTermId());
                mappingRepository.delete(mapping);
            }
        }
        return result;
    }
    */

    @Override
    public Mapping addReviewToMapping(String mappingId, String comment, int noReviewsRequired, Provenance provenance) {
        log.info("Adding review to mapping: {}", mappingId);
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }
        Mapping mapping = mappingOp.get();
        mapping.setStatus(MappingStatus.REVIEW_IN_PROGRESS.name());
        mapping.addReview(new Review(comment, provenance), noReviewsRequired);
        mapping = mappingRepository.save(mapping);
        return mapping;
    }

    @Override
    public Mapping retrieveMappingById(String mappingId) {
        log.info("Retrieving mapping: {}", mappingId);
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }
        return mappingOp.get();
    }

    @Override
    public Mapping addCommentToMapping(String mappingId, String body, Provenance provenance) {
        log.info("Adding comment to mapping: {}", mappingId);
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }
        Mapping mapping = mappingOp.get();
        if (mapping.getComments() == null) {
            mapping.setComments(new ArrayList<>());
        }
        mapping.getComments().add(new Comment(body, provenance));
        mapping = mappingRepository.save(mapping);
        return mapping;
    }

}

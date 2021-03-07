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
        Optional<Mapping> mappingOptional = mappingRepository.findByEntityId(entity.getId());
        if (mappingOptional.isPresent()) {
            if (mappingOptional.get().getOntologyTermIds().contains(ontologyTerm.getId())) {
                log.warn("Mapping for between entity [{}] and ontology term [{}] already exists: {}", entity.getName(), ontologyTerm.getCurie(), mappingOptional.get().getId());
                return mappingOptional.get();
            }
        }

        Mapping created = mappingRepository.insert(new Mapping(null, entity.getId(), Arrays.asList(new String[]{ontologyTerm.getId()}), entity.getProjectId(),
                false, new ArrayList<>(), new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(), provenance, null));
        created.setOntologyTerms(Arrays.asList(new OntologyTerm[]{ontologyTerm}));
        log.info("Mapping for between entity [{}] and ontology term [{}] created: {}", entity.getName(), ontologyTerm.getCurie(), created.getId());
        return created;
    }

    @Override
    public Mapping updateMapping(String mappingId, List<String> ontologyTermIds) {
        log.info("Updating mapping [{}]: {}", mappingId, ontologyTermIds);
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }

        Mapping mapping = mappingOp.get();
        mapping.setOntologyTermIds(ontologyTermIds);
        return mappingRepository.save(mapping);
    }

    @Override
    public void deleteMapping(String mappingId) {
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }

        mappingRepository.delete(mappingOp.get());
    }

    @Override
    public Map<String, Mapping> retrieveMappingsForEntities(List<String> entityIds) {
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
        Map<String, Mapping> result = new HashMap<>();
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
            result.put(mapping.getEntityId(), mapping);
        }
        return result;
    }

    @Override
    public Mapping retrieveMappingForEntity(String entityId) {
        log.info("Retrieving mapping for entity: {}", entityId);
        Optional<Mapping> mappingOptional = mappingRepository.findByEntityId(entityId);
        if (mappingOptional.isPresent()) {
            Mapping mapping = mappingOptional.get();
            Map<String, OntologyTerm> ontologyTermMap = ontologyTermService.retrieveTerms(mapping.getOntologyTermIds());

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
            return mapping;
        }

        log.warn("Unable to find mapping for entity: {}", entityId);
        return null;
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

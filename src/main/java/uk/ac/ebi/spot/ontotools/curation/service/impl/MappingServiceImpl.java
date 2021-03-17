package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.AuditEntryConstants;
import uk.ac.ebi.spot.ontotools.curation.constants.MappingStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Review;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Comment;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.service.AuditEntryService;
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

    @Autowired
    private AuditEntryService auditEntryService;

    @Override
    public Mapping createMapping(Entity entity, List<OntologyTerm> ontologyTerms, Provenance provenance) {
        log.info("Creating mapping for entity [{}]: {}", entity.getName(), ontologyTerms);
        List<String> ontologyTermIds = ontologyTerms.stream().map(OntologyTerm::getId).collect(Collectors.toList());
        Mapping created = mappingRepository.insert(new Mapping(null, entity.getId(), ontologyTermIds, entity.getProjectId(),
                false, new ArrayList<>(), new ArrayList<>(), MappingStatus.AWAITING_REVIEW.name(), provenance, null));
        created.setOntologyTerms(ontologyTerms);

        for (OntologyTerm ontologyTerm : ontologyTerms) {
            auditEntryService.addEntry(AuditEntryConstants.ADDED_MAPPING.name(), entity.getId(), provenance, Map.of(ontologyTerm.getIri(), ontologyTerm.getLabel()));
        }
        log.info("Mapping for between entity [{}] and ontology term [{}] created: {}", entity.getName(), ontologyTerms, created.getId());
        return created;
    }

    @Override
    public Mapping updateMapping(String mappingId, List<OntologyTerm> ontologyTerms, Provenance provenance) {
        log.info("Updating mapping [{}]: {}", mappingId, ontologyTerms);
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }

        Mapping mapping = mappingOp.get();
        List<String> ontologyTermIds = new ArrayList<>();
        mapping.setOntologyTermIds(ontologyTermIds);
        Map<String, String> metadata = new LinkedHashMap<>();
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            ontologyTermIds.add(ontologyTerm.getId());
            metadata.put(ontologyTerm.getCurie(), ontologyTerm.getLabel());
        }

        auditEntryService.addEntry(AuditEntryConstants.UPDATED_MAPPING.name(), mappingOp.get().getEntityId(), provenance, metadata);
        return mappingRepository.save(mapping);
    }

    @Override
    public void deleteMapping(String mappingId, Provenance provenance, Map<String, String> metadata) {
        Optional<Mapping> mappingOp = mappingRepository.findById(mappingId);
        if (!mappingOp.isPresent()) {
            log.error("Mapping not found: {}", mappingId);
            throw new EntityNotFoundException("Mapping not found: " + mappingId);
        }

        mappingRepository.delete(mappingOp.get());
        auditEntryService.addEntry(AuditEntryConstants.REMOVED_MAPPING.name(), mappingOp.get().getEntityId(), provenance, metadata);
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
        auditEntryService.addEntry(AuditEntryConstants.REVIEWED.name(), mappingOp.get().getEntityId(), provenance,
                comment != null ? Map.of("COMMENT", comment) : new HashMap<>());
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

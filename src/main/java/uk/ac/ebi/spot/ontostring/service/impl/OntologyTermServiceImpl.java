package uk.ac.ebi.spot.ontostring.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.constants.TermStatus;
import uk.ac.ebi.spot.ontostring.domain.ProjectContext;
import uk.ac.ebi.spot.ontostring.domain.ProjectContextGraphRestriction;
import uk.ac.ebi.spot.ontostring.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontostring.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontostring.repository.OntologyTermContextRepository;
import uk.ac.ebi.spot.ontostring.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontostring.service.OLSService;
import uk.ac.ebi.spot.ontostring.service.OntologyTermService;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;

import java.util.*;

@Service
public class OntologyTermServiceImpl implements OntologyTermService {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermService.class);

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private OntologyTermContextRepository ontologyTermContextRepository;

    @Autowired
    private OLSService olsService;

    @Override
    public OntologyTerm createTerm(OntologyTerm term, String projectId, String context, String status) {
        if (status == null) {
            status = TermStatus.NEEDS_CREATION.name();
        }

        log.info("Creating term manually: {}", term.getCurie());
        Optional<OntologyTerm> ontologyTermOp = ontologyTermRepository.findByIriHash(DigestUtils.sha256Hex(term.getIri()));
        if (ontologyTermOp.isPresent()) {
            OntologyTerm existing = ontologyTermOp.get();
            Optional<OntologyTermContext> optionalOntologyTermContext = ontologyTermContextRepository.findByOntologyTermIdAndProjectIdAndContext(existing.getId(), projectId, context);
            if (optionalOntologyTermContext.isPresent()) {
                log.warn("Ontology term already exists: {} [{}] | {} [{}]", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel(), projectId, context);
                existing.setStatus(optionalOntologyTermContext.get().getStatus());
                return existing;
            }
            log.info("Ontology term already exists, but in a different context: {} [{}] | {} [{}]", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel(), projectId, context);
            OntologyTermContext newContext = ontologyTermContextRepository.insert(new OntologyTermContext(
                    null, existing.getId(), projectId, context, status, new ArrayList<>(), false
            ));

            existing.getOntoTermContexts().add(newContext.getId());
            existing = ontologyTermRepository.save(existing);
            existing.setStatus(newContext.getStatus());
            return existing;
        }

        OntologyTerm ontologyTerm = ontologyTermRepository.insert(term);
        OntologyTermContext newContext = ontologyTermContextRepository.insert(new OntologyTermContext(
                null, ontologyTerm.getId(), projectId, context, status, new ArrayList<>(), false
        ));

        ontologyTerm.getOntoTermContexts().add(newContext.getId());
        ontologyTerm = ontologyTermRepository.save(ontologyTerm);
        ontologyTerm.setStatus(newContext.getStatus());
        log.info("Term [{}] created: {}", ontologyTerm.getCurie(), ontologyTerm.getId());
        return ontologyTerm;
    }

    @Override
    public OntologyTerm createTerm(String iri, ProjectContext projectContext) {
        log.info("Creating term: {}", iri);
        try {
            OntologyTerm ot = null;
            Optional<OntologyTerm> ontologyTermOp = ontologyTermRepository.findByIriHash(DigestUtils.sha256Hex(iri));
            if (ontologyTermOp.isPresent()) {
                Optional<OntologyTermContext> optionalOntologyTermContext = ontologyTermContextRepository.findByOntologyTermIdAndProjectIdAndContext(
                        ontologyTermOp.get().getId(), projectContext.getProjectId(), projectContext.getName());
                if (optionalOntologyTermContext.isPresent()) {
                    log.warn("Ontology term already exists: {} [{}] | {} [{}]", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel(),
                            projectContext.getProjectId(), optionalOntologyTermContext.get().getStatus());
                    OntologyTerm ontologyTerm = ontologyTermOp.get();
                    ontologyTerm.setStatus(optionalOntologyTermContext.get().getStatus());
                    return ontologyTerm;
                } else {
                    ot = ontologyTermOp.get();
                }
            }

            List<OLSTermDto> preferredOntoResponse = new ArrayList<>();
            for (String preferredOntology : projectContext.getPreferredMappingOntologies()) {
                List<OLSTermDto> termList = olsService.retrieveTerms(preferredOntology, iri);
                if (projectContext.getProjectContextGraphRestriction() != null) {
                    termList = this.filterGraphRestriction(termList, preferredOntology, projectContext.getProjectContextGraphRestriction());
                }
                preferredOntoResponse.addAll(termList);
            }

            List<OLSTermDto> parentOntoResponse = new ArrayList<>();
            String ontoId = CurationUtil.ontoFromIRI(iri);
            String termStatus;
            if (!projectContext.getPreferredMappingOntologiesLower().contains(ontoId.toLowerCase())) {
                parentOntoResponse = olsService.retrieveTerms(ontoId, iri);
                if (projectContext.getProjectContextGraphRestriction() != null) {
                    parentOntoResponse = this.filterGraphRestriction(parentOntoResponse, ontoId, projectContext.getProjectContextGraphRestriction());
                }

                termStatus = parseStatus(preferredOntoResponse, parentOntoResponse, null);
            } else {
                termStatus = parseStatus(preferredOntoResponse, null, null);
            }

            if (termStatus.equalsIgnoreCase(TermStatus.DELETED.name())) {
                log.warn("Found DELETED term: {}", iri);
                return null;
            } else {
                OLSTermDto olsTermDto = termStatus.equalsIgnoreCase(TermStatus.CURRENT.name()) ? preferredOntoResponse.get(0) : parentOntoResponse.get(0);
                if (ot != null) {
                    OntologyTermContext newContext = ontologyTermContextRepository.insert(new OntologyTermContext(
                            null, ot.getId(), projectContext.getProjectId(), projectContext.getName(), termStatus, new ArrayList<>(), false
                    ));

                    ot.getOntoTermContexts().add(newContext.getId());
                    ot = ontologyTermRepository.save(ot);
                    ot.setStatus(newContext.getStatus());
                    log.info("Updated ontology term [{} | {}]: {} | {} | {}", ot.getCurie(), ot.getLabel(), projectContext.getProjectId(),
                            projectContext.getName(), termStatus);
                } else {
                    ot = new OntologyTerm(null, olsTermDto.getCurie(), olsTermDto.getIri(),
                            DigestUtils.sha256Hex(iri), olsTermDto.getLabel(),
                            null, null, new ArrayList<>(), null);
                    ot = ontologyTermRepository.insert(ot);
                    OntologyTermContext newContext = ontologyTermContextRepository.insert(new OntologyTermContext(
                            null, ot.getId(), projectContext.getProjectId(), projectContext.getName(), termStatus, new ArrayList<>(), false
                    ));

                    ot.getOntoTermContexts().add(newContext.getId());
                    ot = ontologyTermRepository.save(ot);
                    ot.setStatus(newContext.getStatus());
                    log.info("Created ontology term [{} | {}]: {}", ot.getCurie(), ot.getLabel(), ot.getId());
                }
            }

            return ot;
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
        }

        return null;
    }

    private List<OLSTermDto> filterGraphRestriction(List<OLSTermDto> termList, String ontoId,
                                                    ProjectContextGraphRestriction projectContextGraphRestriction) {
        List<OLSTermDto> results = new ArrayList<>();
        for (OLSTermDto olsTermDto : termList) {
            List<OLSTermDto> ancestors = olsService.retrieveAncestors(ontoId, olsTermDto.getIri(), projectContextGraphRestriction.getDirect());
            if (this.isGraphRestrictionValid(olsTermDto.getCurie(), ancestors, projectContextGraphRestriction.getClasses(), projectContextGraphRestriction.getIncludeSelf())) {
                results.add(olsTermDto);
            }
        }
        return results;
    }

    private boolean isGraphRestrictionValid(String curie, List<OLSTermDto> ancestors, List<String> classes, Boolean includeSelf) {
        for (OLSTermDto ancestor : ancestors) {
            if (classes.contains(ancestor.getCurie())) {
                if (!includeSelf) {
                    if (!ancestor.getCurie().equals(curie)) {
                        return true;
                    }
                } else {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public String retrieveStatusUpdate(String iri, ProjectContext projectContext, String previousStatus) {
        List<OLSTermDto> preferredOntoResponse = new ArrayList<>();
        for (String preferredOntology : projectContext.getPreferredMappingOntologies()) {
            preferredOntoResponse.addAll(olsService.retrieveTerms(preferredOntology, iri));
        }
        String ontoId = CurationUtil.ontoFromIRI(iri);
        String termStatus;
        if (!projectContext.getPreferredMappingOntologiesLower().contains(ontoId.toLowerCase())) {
            List<OLSTermDto> parentOntoResponse = olsService.retrieveTerms(ontoId, iri);
            termStatus = parseStatus(preferredOntoResponse, parentOntoResponse, previousStatus);
        } else {
            termStatus = parseStatus(preferredOntoResponse, null, previousStatus);
        }
        return termStatus;
    }

    @Override
    public Page<OntologyTermContext> retrieveMappedTermsByStatus(String projectId, String context, String status, Pageable pageable) {
        log.info("Retrieving terms by status: {} | {} | {}", projectId, context, status);
        Page<OntologyTermContext> ontologyTermContextPage =
                context == null ?
                        ontologyTermContextRepository.findByHasMappingAndProjectIdAndStatus(true, projectId, status, pageable) :
                        ontologyTermContextRepository.findByHasMappingAndProjectIdAndContextAndStatus(true, projectId, context, status, pageable);
        return ontologyTermContextPage;
    }

    @Override
    public Map<String, Integer> retrieveTermStats(String projectId, String context) {
        log.info("Retrieving term stats for: {} | {}", projectId, context);
        Map<String, Integer> stats = new LinkedHashMap<>();
        long count = ontologyTermContextRepository.countByHasMappingAndProjectIdAndContextAndStatus(true, projectId, context, TermStatus.NEEDS_IMPORT.name());
        stats.put(TermStatus.NEEDS_IMPORT.name(), (int) count);

        count = ontologyTermContextRepository.countByHasMappingAndProjectIdAndContextAndStatus(true, projectId, context, TermStatus.NEEDS_CREATION.name());
        stats.put(TermStatus.NEEDS_CREATION.name(), (int) count);
        return stats;
    }

    @Override
    public OntologyTerm mapTerm(OntologyTerm ontologyTerm, Mapping mapping, boolean add) {
        log.info("Adding mapping to term: {} | [{} :: {}] | {}", ontologyTerm.getId(), mapping.getProjectId(), mapping.getContext(), mapping.getId());
        Optional<OntologyTermContext> optionalOntologyTermContext = ontologyTermContextRepository.findByOntologyTermIdAndProjectIdAndContext(ontologyTerm.getId(),
                mapping.getProjectId(), mapping.getContext());
        if (!optionalOntologyTermContext.isPresent()) {
            log.warn("Onto term context missing: {} | {} [{}]", ontologyTerm.getId(), mapping.getProjectId(), mapping.getContext());
            return ontologyTerm;
        }

        OntologyTermContext ontologyTermContext = optionalOntologyTermContext.get();
        if (add) {
            if (!ontologyTermContext.getMappings().contains(mapping.getId())) {
                ontologyTermContext.getMappings().add(mapping.getId());
            }
            ontologyTermContext.setHasMapping(true);
        } else {
            if (ontologyTermContext.getMappings().contains(mapping.getId())) {
                ontologyTermContext.getMappings().remove(mapping.getId());
            }
            if (ontologyTermContext.getMappings().isEmpty()) {
                ontologyTermContext.setHasMapping(false);
            }
        }
        ontologyTermContext = ontologyTermContextRepository.save(ontologyTermContext);
        ontologyTerm.setStatus(ontologyTermContext.getStatus());
        return ontologyTerm;
    }

    @Override
    public Map<String, OntologyTerm> retrieveTerms(List<String> ontologyTermIds, String projectId, String context) {
        log.info("Retrieving {} ontology terms.", ontologyTermIds.size());
        Map<String, OntologyTerm> result = new HashMap<>();
        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findByIdIn(ontologyTermIds);
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            Optional<OntologyTermContext> ontologyTermContextOp = ontologyTermContextRepository.findByOntologyTermIdAndProjectIdAndContext(ontologyTerm.getId(),
                    projectId, context);
            if (ontologyTermContextOp.isPresent()) {
                ontologyTerm.setStatus(ontologyTermContextOp.get().getStatus());
            }
            result.put(ontologyTerm.getId(), ontologyTerm);
        }
        return result;
    }

    @Override
    public OntologyTerm retrieveTermByCurie(String curie) {
        log.info("Retrieving ontology term: {}", curie);
        Optional<OntologyTerm> ontologyTermOp = ontologyTermRepository.findByCurie(curie);
        if (!ontologyTermOp.isPresent()) {
            log.error("Unable to find ontology term: {}", curie);
            throw new EntityNotFoundException("Unable to find ontology term: " + curie);
        }
        return ontologyTermOp.get();
    }

    @Override
    public OntologyTerm retrieveTermById(String ontologyTermId) {
        log.info("Retrieving ontology term: {}", ontologyTermId);
        Optional<OntologyTerm> ontologyTermOp = ontologyTermRepository.findById(ontologyTermId);
        if (!ontologyTermOp.isPresent()) {
            log.error("Unable to find ontology term: {}", ontologyTermId);
            throw new EntityNotFoundException("Unable to find ontology term: " + ontologyTermId);
        }
        return ontologyTermOp.get();
    }

    @Override
    public List<OntologyTerm> retrieveTermByCuries(List<String> curies) {
        log.info("Retrieving ontology terms: {}", curies);
        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findByCurieIn(curies);
        log.info("Found {} ontology terms.", ontologyTerms.size());
        return ontologyTerms;
    }

    private String parseStatus(List<OLSTermDto> preferredOntoResponse, List<OLSTermDto> parentOntoResponse, String previousState) {
        if (CollectionUtils.isEmpty(preferredOntoResponse) && CollectionUtils.isEmpty(parentOntoResponse)) {
            return TermStatus.DELETED.name();
        }

        if ((!CollectionUtils.isEmpty(preferredOntoResponse) && preferredOntoResponse.get(0).getObsolete()) ||
                (!CollectionUtils.isEmpty(parentOntoResponse) && parentOntoResponse.get(0).getObsolete())) {
            return TermStatus.OBSOLETE.name();
        }

        if (!CollectionUtils.isEmpty(preferredOntoResponse)) {
            return TermStatus.CURRENT.name();
        }
        if (previousState == null) {
            return TermStatus.NEEDS_IMPORT.name();
        }
        return previousState;
    }
}

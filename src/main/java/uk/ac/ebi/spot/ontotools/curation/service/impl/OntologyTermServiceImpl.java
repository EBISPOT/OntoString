package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.*;

@Service
public class OntologyTermServiceImpl implements OntologyTermService {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermService.class);

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private OLSService olsService;

    @Override
    public OntologyTerm createTerm(OntologyTerm term) {
        log.info("Creating term manually: {}", term.getCurie());
        Optional<OntologyTerm> ontologyTermOp = ontologyTermRepository.findByIriHash(DigestUtils.sha256Hex(term.getIri()));
        if (ontologyTermOp.isPresent()) {
            OntologyTerm existing = ontologyTermOp.get();
            OntologyTermContext ontologyTermContext = term.getContexts().get(0);
            for (OntologyTermContext existingOTC : existing.getContexts()) {
                if (existingOTC.getContext().equals(ontologyTermContext.getContext())) {
                    log.warn("Ontology term already exists: {} | {} | {}", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel(), ontologyTermContext.getContext());
                    return existing;
                }
            }

            log.info("Ontology term already exists, but in a different context: {} | {} | {}", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel(), ontologyTermContext.getContext());
            existing.getContexts().add(ontologyTermContext);
            existing = ontologyTermRepository.save(existing);
            return existing;
        }

        OntologyTerm ontologyTerm = ontologyTermRepository.insert(term);
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
                String status = CurationUtil.termStatusForContext(ontologyTermOp.get(), projectContext.getProjectId(), projectContext.getName());
                if (status != null) {
                    log.warn("Ontology term already exists: {} | {} | {}", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel(), status);
                    return ontologyTermOp.get();
                } else {
                    ot = ontologyTermOp.get();
                }
            }

            List<OLSTermDto> preferredOntoResponse = new ArrayList<>();
            for (String preferredOntology : projectContext.getPreferredMappingOntologies()) {
                preferredOntoResponse.addAll(olsService.retrieveTerms(preferredOntology, iri));
            }
            List<OLSTermDto> parentOntoResponse = new ArrayList<>();
            String ontoId = CurationUtil.ontoFromIRI(iri);
            String termStatus;
            if (!projectContext.getPreferredMappingOntologiesLower().contains(ontoId.toLowerCase())) {
                parentOntoResponse = olsService.retrieveTerms(ontoId, iri);
                termStatus = parseStatus(preferredOntoResponse, parentOntoResponse, null);
            } else {
                termStatus = parseStatus(preferredOntoResponse, null, null);
            }

            if (termStatus.equalsIgnoreCase(TermStatus.DELETED.name())) {
                log.warn("Found DELETED term: {}", iri);
                return null;
            } else {
                if (ot != null) {
                    if (ot.getContexts() == null) {
                        ot.setContexts(new ArrayList<>());
                    }
                }

                OntologyTermContext otc = new OntologyTermContext(projectContext.getProjectId(), projectContext.getName(), termStatus);
                OLSTermDto olsTermDto = termStatus.equalsIgnoreCase(TermStatus.CURRENT.name()) ? preferredOntoResponse.get(0) : parentOntoResponse.get(0);
                if (ot != null) {
                    ot.getContexts().add(otc);
                    ot = ontologyTermRepository.save(ot);
                    log.info("Updated ontology term [{} | {}]: {} | {} | {}", ot.getCurie(), ot.getLabel(), projectContext.getProjectId(),
                            projectContext.getName(), termStatus);
                } else {
                    ot = new OntologyTerm(null, olsTermDto.getCurie(), olsTermDto.getIri(),
                            DigestUtils.sha256Hex(iri), olsTermDto.getLabel(),
                            Arrays.asList(new OntologyTermContext[]{otc}), null, null);
                    ot = ontologyTermRepository.insert(ot);
                    log.info("Created ontology term [{} | {}]: {}", ot.getCurie(), ot.getLabel(), ot.getId());
                }
            }

            return ot;
        } catch (Exception e) {
            log.error("ERROR: {}", e.getMessage(), e);
        }

        return null;
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
    public List<OntologyTerm> retrieveTermsByStatus(String projectId, String context, List<String> statusList) {
        log.info("Retrieving terms by status: {} | {} | {}", projectId, context, statusList);
        List<OntologyTerm> finalList = new ArrayList<>();
        for (String status : statusList) {
            finalList.addAll(ontologyTermRepository.findByContexts_ProjectIdAndContexts_ContextAndContexts_Status(projectId, context, status));
        }
        log.info("Found {} terms.", finalList.size());
        return finalList;
    }

    @Override
    public Map<String, OntologyTerm> retrieveTerms(List<String> ontologyTermIds) {
        log.info("Retrieving {} ontology terms.", ontologyTermIds.size());
        Map<String, OntologyTerm> result = new HashMap<>();
        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findByIdIn(ontologyTermIds);
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            result.put(ontologyTerm.getId(), ontologyTerm);
        }
        return result;
    }

    @Override
    public List<OntologyTerm> retrieveAllTerms() {
        log.info("Retrieving all ontology terms.");
        return ontologyTermRepository.findAll();
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

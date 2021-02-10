package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.Project;
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
    public OntologyTerm createTerm(String iri, Project project) {
        log.info("Creating term: {}", iri);
        Optional<OntologyTerm> ontologyTermOp = ontologyTermRepository.findByIriHash(DigestUtils.sha256Hex(iri));
        if (ontologyTermOp.isPresent()) {
            log.warn("Ontology term already exists: {} | {}", ontologyTermOp.get().getCurie(), ontologyTermOp.get().getLabel());
            return ontologyTermOp.get();
        }

        List<OLSTermDto> preferredOntoResponse = olsService.retrieveTerms(project.getPreferredMappintOntology(), iri);
        List<OLSTermDto> parentOntoResponse = new ArrayList<>();
        String ontoId = CurationUtil.ontoFromIRI(iri);
        String termStatus;
        if (!ontoId.equalsIgnoreCase(project.getPreferredMappintOntology())) {
            parentOntoResponse = olsService.retrieveTerms(ontoId, iri);
            termStatus = parseStatus(preferredOntoResponse, parentOntoResponse, null);
        } else {
            termStatus = parseStatus(preferredOntoResponse, null, null);
        }

        OntologyTerm ot;
        if (termStatus.equalsIgnoreCase(TermStatus.DELETED.name())) {
            /**
             * TODO: Discuss
             *
             * Previous code:
             * ot = new OntologyTerm(null, "Not found", iri, DigestUtils.sha256Hex(iri), "Not found", MappingStatus.DELETED.name(), null, null);
             */
            log.warn("Found DELETED term: {}", iri);
            return null;
        } else {
            if (termStatus.equalsIgnoreCase(TermStatus.CURRENT.name())) {
                OLSTermDto olsTermDto = preferredOntoResponse.get(0);
                ot = new OntologyTerm(null, olsTermDto.getCurie(), iri,
                        DigestUtils.sha256Hex(iri), olsTermDto.getLabel(), TermStatus.CURRENT.name(), null, null);
            } else {
                OLSTermDto olsTermDto = parentOntoResponse.get(0);
                ot = new OntologyTerm(null, olsTermDto.getCurie(), iri, DigestUtils.sha256Hex(iri), olsTermDto.getLabel(), termStatus, null, null);
            }
        }

        ot = ontologyTermRepository.insert(ot);
        log.info("Created ontology term [{} | {}]: {}", ot.getCurie(), ot.getLabel(), ot.getId());
        return ot;
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

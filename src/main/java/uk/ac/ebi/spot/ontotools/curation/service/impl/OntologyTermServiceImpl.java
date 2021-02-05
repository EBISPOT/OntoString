package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class OntologyTermServiceImpl implements OntologyTermService {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermService.class);

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Override
    public Map<String, OntologyTerm> getOntologyTermsById(List<String> ontoTermIds) {
        log.info("Retrieving ontology terms for {} ids.", ontoTermIds.size());
        Map<String, OntologyTerm> mappingMap = new HashMap<>();
        List<OntologyTerm> ontologyTerms = ontologyTermRepository.findByIdIn(ontoTermIds);
        for (OntologyTerm ontologyTerm : ontologyTerms) {
            mappingMap.put(ontologyTerm.getId(), ontologyTerm);
        }
        return mappingMap;
    }

    /**
     * TODO: Implement
     */
    @Override
    public OntologyTerm createTerm(String iri) {
        return null;
    }
}

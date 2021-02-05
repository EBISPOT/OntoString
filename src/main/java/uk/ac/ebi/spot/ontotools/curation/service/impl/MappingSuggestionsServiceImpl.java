package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.service.MappingSuggestionsService;

import java.util.List;

@Service
public class MappingSuggestionsServiceImpl implements MappingSuggestionsService {

    private static final Logger log = LoggerFactory.getLogger(MappingSuggestionsService.class);


    /**
     * TODO: Implement
     */
    @Override
    public void createMappingSuggestion(Entity entity, OntologyTerm ontologyTerm) {
    }

    /**
     * TODO: Implement
     */
    @Override
    @Async
    public void deleteMappingSuggestionsExcluding(Entity entity, List<OntologyTerm> ontologyTerms) {

    }
}

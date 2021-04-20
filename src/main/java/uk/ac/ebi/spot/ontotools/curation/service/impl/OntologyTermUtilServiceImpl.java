package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Comment;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermUtilService;

import java.util.List;
import java.util.stream.Stream;

@Service
public class OntologyTermUtilServiceImpl implements OntologyTermUtilService {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermUtilService.class);

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private MappingRepository mappingRepository;

    @Override
    public void actionTerms(String projectId, String context, String status, String comment, User user) {
        log.info("Updating status for terms: {} | {} | {}", projectId, context, status);
        Stream<OntologyTerm> ontologyTermStream = ontologyTermRepository.readByContexts_ProjectIdAndContexts_ContextAndContexts_Status(projectId, context, status);
        ontologyTermStream.forEach(ontologyTerm -> this.updateStatus(ontologyTerm, status, projectId, context, comment, user));
        ontologyTermStream.close();
    }

    private void updateStatus(OntologyTerm ontologyTerm, String oldStatus, String projectId, String context, String comment, User user) {
        String newStatus = TermStatus.AWAITING_IMPORT.name();
        if (oldStatus.equals(TermStatus.NEEDS_CREATION.name())) {
            newStatus = TermStatus.AWAITING_CREATION.name();
        }

        List<OntologyTermContext> ontologyTermContexts = ontologyTerm.getContexts();
        OntologyTermContext found = null;
        for (OntologyTermContext ontologyTermContext : ontologyTermContexts) {
            if (ontologyTermContext.getProjectId().equals(projectId) && ontologyTermContext.getContext().equals(context)) {
                found = ontologyTermContext;
                break;
            }
        }

        if (found != null) {
            ontologyTerm.getContexts().remove(found);
            ontologyTerm.getContexts().add(new OntologyTermContext(projectId, context, newStatus));
            ontologyTermRepository.save(ontologyTerm);

            List<Mapping> mappings = mappingRepository.findByProjectIdAndContextAndOntologyTermIdsContains(projectId, context, ontologyTerm.getId());
            for (Mapping mapping : mappings) {
                mapping.getComments().add(new Comment(comment, new Provenance(user.getName(), user.getEmail(), DateTime.now())));
                mappingRepository.save(mapping);
            }
        } else {
            log.error("Unable to find context [{}] for ontology term: {} | {}", context, ontologyTerm.getCurie(), projectId);
        }
    }
}

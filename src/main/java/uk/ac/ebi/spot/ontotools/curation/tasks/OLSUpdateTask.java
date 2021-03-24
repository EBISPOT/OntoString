package uk.ac.ebi.spot.ontotools.curation.tasks;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.log.OntologyTermUpdateLogBatch;
import uk.ac.ebi.spot.ontotools.curation.domain.log.OntologyTermUpdateLogEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermUpdateLogBatchRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermUpdateLogEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "ontotools.ols.update-schedule.enabled", havingValue = "true")
public class OLSUpdateTask {

    private static final Logger log = LoggerFactory.getLogger(OLSUpdateTask.class);

    /**
     * Scheduled task to periodically go through all local terms with status CURRENT | AWAITING_IMPORT or NEEDS_IMPORT
     * and repeat the process associated with checking the status - as per the initial term creation
     */

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private OntologyTermUpdateLogBatchRepository ontologyTermUpdateLogBatchRepository;

    @Autowired
    private OntologyTermUpdateLogEntryRepository ontologyTermUpdateLogEntryRepository;

    @Autowired
    private OLSService olsService;

    @Autowired
    private MappingService mappingService;

    @Scheduled(cron = "${ontotools.ols.update-schedule.pattern}")
    public void updateOntologyTerms() {
        log.info("Running ontology terms update ...");
        double sTime = System.currentTimeMillis();
        List<String> targetStatusList = Arrays.asList(new String[]{
                TermStatus.CURRENT.name(),
                TermStatus.AWAITING_IMPORT.name(),
                TermStatus.NEEDS_IMPORT.name(),
                TermStatus.NEEDS_CREATION.name()
        });

        OntologyTermUpdateLogBatch ontologyTermUpdateLogBatch = ontologyTermUpdateLogBatchRepository.insert(new OntologyTermUpdateLogBatch(null, DateTime.now(), 0));

        Stream<OntologyTerm> ontologyTermStream = ontologyTermRepository.readByStatusIn(targetStatusList);
        ontologyTermStream.forEach(ontologyTerm -> this.updateTerm(ontologyTerm, ontologyTermUpdateLogBatch.getId()));
        ontologyTermStream.close();

        double eTime = System.currentTimeMillis();
        double tTime = (eTime - sTime) / 1000;
        ontologyTermUpdateLogBatch.setTotalTime((int) tTime);
        ontologyTermUpdateLogBatchRepository.save(ontologyTermUpdateLogBatch);
        log.info("Ontology terms update finalized in {}s.", tTime);
    }

    /**
     * TODO: Fix this to cater for transitions starting from NEEDS_IMPORT / AWAITING_IMPORT into CURRENT
     */

    private void updateTerm(OntologyTerm ontologyTerm, String batchId) {
        String currentStatus = ontologyTerm.getStatus();
        String newStatus = currentStatus;
        OLSTermDto olsTermDto = olsService.retrieveOriginalTerm(ontologyTerm.getIri());
        if (olsTermDto == null) {
            newStatus = TermStatus.DELETED.name();
        } else {
            if (olsTermDto.getObsolete() != null && olsTermDto.getObsolete()) {
                newStatus = TermStatus.OBSOLETE.name();
            }
        }

        if (!currentStatus.equalsIgnoreCase(newStatus)) {
            ontologyTermUpdateLogEntryRepository.insert(new OntologyTermUpdateLogEntry(null, batchId, ontologyTerm.getId(),
                    ontologyTerm.getCurie(), ontologyTerm.getLabel(), currentStatus, newStatus));
            ontologyTerm.setStatus(newStatus);
            ontologyTermRepository.save(ontologyTerm);

            mappingService.updateStatusForObsoleteMappings(ontologyTerm.getId());
        }
    }
}

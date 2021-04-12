package uk.ac.ebi.spot.ontotools.curation.tasks;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontotools.curation.constants.TermStatus;
import uk.ac.ebi.spot.ontotools.curation.constants.UpdateTaskType;
import uk.ac.ebi.spot.ontotools.curation.domain.Project;
import uk.ac.ebi.spot.ontotools.curation.domain.ProjectContext;
import uk.ac.ebi.spot.ontotools.curation.domain.UpdateTask;
import uk.ac.ebi.spot.ontotools.curation.domain.log.OntologyTermUpdateLogBatch;
import uk.ac.ebi.spot.ontotools.curation.domain.log.OntologyTermUpdateLogEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTermContext;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermUpdateLogBatchRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermUpdateLogEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.*;

import java.util.*;
import java.util.stream.Stream;

@Component
@ConditionalOnProperty(name = "ontotools.ols.update-schedule.enabled", havingValue = "true")
public class OntologyTermUpdateTask {

    private static final Logger log = LoggerFactory.getLogger(OntologyTermUpdateTask.class);

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

    @Autowired
    private ProjectService projectService;

    @Autowired
    private UserService userService;

    @Autowired
    private OntologyTermService ontologyTermService;

    @Autowired
    private UpdateTaskManager updateTaskManager;

    @Scheduled(cron = "${ontotools.ols.update-schedule.pattern}")
    public void updateOntologyTerms() {
        log.info("Running ontology terms update ...");
        UpdateTask updateTask = updateTaskManager.checkAndCreateIfNecessary(UpdateTaskType.TERM_UPDATE.name());
        if (updateTask == null) {
            return;
        }

        double sTime = System.currentTimeMillis();
        OntologyTermUpdateLogBatch ontologyTermUpdateLogBatch = ontologyTermUpdateLogBatchRepository.insert(new OntologyTermUpdateLogBatch(null, DateTime.now(), 0));

        List<Project> projects = projectService.retrieveProjects(userService.retrieveRobotUser());
        Map<String, Map<String, ProjectContext>> projectConfig = prepareProjectConfig(projects);

        Stream<OntologyTerm> ontologyTermStream = ontologyTermRepository.findAllByCustomQueryAndStream();
        ontologyTermStream.forEach(ontologyTerm -> this.updateTerm(ontologyTerm, projectConfig, ontologyTermUpdateLogBatch.getId()));
        ontologyTermStream.close();

        double eTime = System.currentTimeMillis();
        double tTime = (eTime - sTime) / 1000;
        ontologyTermUpdateLogBatch.setTotalTime((int) tTime);
        ontologyTermUpdateLogBatchRepository.save(ontologyTermUpdateLogBatch);
        log.info("Ontology terms update finalized in {}s.", tTime);
        updateTaskManager.removeUpdateTask(updateTask);
    }

    private Map<String, Map<String, ProjectContext>> prepareProjectConfig(List<Project> projects) {
        Map<String, Map<String, ProjectContext>> result = new LinkedHashMap<>();

        for (Project project : projects) {
            Map<String, ProjectContext> contextMap = new LinkedHashMap<>();
            for (ProjectContext projectContext : project.getContexts()) {
                contextMap.put(projectContext.getName(), projectContext);
            }
            result.put(project.getId(), contextMap);
        }

        return result;
    }

    private void updateTerm(OntologyTerm ontologyTerm, Map<String, Map<String, ProjectContext>> projectConfig, String batchId) {
        Map<String, String> deletedTerms = new LinkedHashMap<>();
        Map<String, String> toUpdate = new LinkedHashMap<>();

        for (OntologyTermContext ontologyTermContext : ontologyTerm.getContexts()) {
            String currentStatus = ontologyTermContext.getStatus();
            String newStatus = ontologyTermService.retrieveStatusUpdate(ontologyTerm.getIri(),
                    projectConfig.get(ontologyTermContext.getProjectId()).get(ontologyTermContext.getContext()), currentStatus);

            if (!newStatus.equalsIgnoreCase(currentStatus)) {
                toUpdate.put(ontologyTermContext.getProjectId() + "::" + ontologyTermContext.getContext(), newStatus);

                if (newStatus.equalsIgnoreCase(TermStatus.DELETED.name())) {
                    deletedTerms.put(ontologyTermContext.getProjectId(), ontologyTermContext.getContext());
                }
            }
        }

        if (!toUpdate.isEmpty()) {
            List<OntologyTermContext> newContexts = new ArrayList<>();
            for (OntologyTermContext ontologyTermContext : ontologyTerm.getContexts()) {
                if (toUpdate.containsKey(ontologyTermContext.getProjectId() + "::" + ontologyTermContext.getContext())) {
                    String newStatus = toUpdate.get(ontologyTermContext.getProjectId() + "::" + ontologyTermContext.getContext());
                    newContexts.add(new OntologyTermContext(ontologyTermContext.getProjectId(),
                            ontologyTermContext.getContext(), newStatus));

                    ontologyTermUpdateLogEntryRepository.insert(new OntologyTermUpdateLogEntry(null, batchId, ontologyTerm.getId(),
                            ontologyTerm.getCurie(), ontologyTerm.getLabel(), ontologyTermContext.getProjectId(),
                            ontologyTermContext.getContext(), ontologyTermContext.getStatus(), newStatus));
                } else {
                    newContexts.add(ontologyTermContext);
                }
            }

            ontologyTerm.setContexts(newContexts);
            ontologyTermRepository.save(ontologyTerm);
        }

        if (!deletedTerms.isEmpty()) {
            for (String projectId : deletedTerms.keySet()) {
                mappingService.updateStatusForObsoleteMappings(ontologyTerm.getId(), projectId, deletedTerms.get(projectId));
            }
        }
    }
}

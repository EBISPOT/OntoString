package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.SourceRepository;
import uk.ac.ebi.spot.ontotools.curation.service.SourceService;

import java.util.List;
import java.util.Optional;

@Service
public class SourceServiceImpl implements SourceService {

    private static final Logger log = LoggerFactory.getLogger(SourceService.class);

    @Autowired
    private SourceRepository sourceRepository;

    @Override
    public Source createSource(Source toCreate, String projectId) {
        log.info("[{}] Creating project: {}", projectId, toCreate.getName());
        toCreate.setProjectId(projectId);
        Source created = sourceRepository.insert(toCreate);
        log.info("[{}] Source created: {}", created.getName(), created.getId());
        return created;
    }

    @Override
    public List<Source> getSources(String projectId) {
        log.info("Retrieving sources for project: {}", projectId);
        List<Source> sources = sourceRepository.findByProjectIdAndArchived(projectId, false);
        log.info("Found {} sources.", sources.size());
        return sources;
    }

    @Override
    public Source getSource(String sourceId, String projectId) {
        log.info("Retrieving sources for project: {}", projectId);
        Optional<Source> sourceOp = sourceRepository.findByIdAndProjectIdAndArchived(sourceId, projectId, false);
        if (!sourceOp.isPresent()) {
            log.error("[{}] Unable to find source: {}", projectId, sourceId);
            throw new EntityNotFoundException("[" + projectId + "] Unable to find source: " + sourceId);
        }
        return sourceOp.get();
    }
}

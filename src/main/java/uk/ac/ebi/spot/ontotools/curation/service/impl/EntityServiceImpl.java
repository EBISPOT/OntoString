package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Entity;
import uk.ac.ebi.spot.ontotools.curation.repository.EntityRepository;
import uk.ac.ebi.spot.ontotools.curation.service.EntityService;

@Service
public class EntityServiceImpl implements EntityService {

    private static final Logger log = LoggerFactory.getLogger(EntityService.class);

    @Autowired
    private EntityRepository entityRepository;

    @Override
    public Entity createEntity(Entity entity) {
        log.info("[{}] Creating entity: {}", entity.getSourceId(), entity.getName());
        Entity created = entityRepository.insert(entity);
        log.info("[{}] Entity created: {}", created.getSourceId(), created.getId());
        return created;
    }
}

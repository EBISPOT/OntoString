package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MappingServiceImpl implements MappingService {

    private static final Logger log = LoggerFactory.getLogger(MappingService.class);

    @Autowired
    private MappingRepository mappingRepository;

    @Override
    public Map<String, Mapping> getMappingsById(List<String> mappingIds) {
        log.info("Retrieving mappings for {} ids.", mappingIds.size());
        Map<String, Mapping> mappingMap = new HashMap<>();
        List<Mapping> mappings = mappingRepository.findByIdIn(mappingIds);
        for (Mapping mapping : mappings) {
            mappingMap.put(mapping.getMappedTermId(), mapping);
        }
        return mappingMap;
    }
}

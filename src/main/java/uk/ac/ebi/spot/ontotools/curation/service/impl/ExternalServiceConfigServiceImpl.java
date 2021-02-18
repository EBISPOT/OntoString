package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;
import uk.ac.ebi.spot.ontotools.curation.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontotools.curation.repository.ExternalServiceConfigRepository;
import uk.ac.ebi.spot.ontotools.curation.service.ConfigRegistry;
import uk.ac.ebi.spot.ontotools.curation.service.ExternalServiceConfigService;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ExternalServiceConfigServiceImpl implements ExternalServiceConfigService {

    private static final Logger log = LoggerFactory.getLogger(ExternalServiceConfigService.class);

    @Autowired
    private ExternalServiceConfigRepository externalServiceConfigRepository;

    @Autowired
    private ConfigRegistry configRegistry;

    @Override
    public Map<String, String> retrieveAliases(String serviceName) {
        log.info("Retrieving aliases for: {}", serviceName);
        Map<String, String> map = new HashMap<>();
        Optional<ExternalServiceConfig> externalServiceConfigOp = externalServiceConfigRepository.findByName(serviceName);
        if (!externalServiceConfigOp.isPresent()) {
            log.error("Unable to find config for service: {}", serviceName);
            return map;
        }

        List<String> aliases = externalServiceConfigOp.get().getAliases();
        return CurationUtil.parseAliases(aliases);
    }

    @Override
    public List<ExternalServiceConfig> retrieveConfigs() {
        log.info("Retrieving config for all services.");
        List<ExternalServiceConfig> list = externalServiceConfigRepository.findAll();
        log.info("Found {} service configs.", list.size());
        return list;
    }

    @Override
    public ExternalServiceConfig updateConfig(ExternalServiceConfig externalServiceConfig) {
        log.info("Updating config for: {}", externalServiceConfig.getName());
        Optional<ExternalServiceConfig> externalServiceConfigOp = externalServiceConfigRepository.findByName(externalServiceConfig.getName());
        if (!externalServiceConfigOp.isPresent()) {
            log.error("Unable to find config for service: {}", externalServiceConfig.getName());
            throw new EntityNotFoundException("Unable to find config for service: " + externalServiceConfig.getName());
        }
        ExternalServiceConfig existing = externalServiceConfigOp.get();
        existing.setAliases(externalServiceConfig.getAliases());
        existing = externalServiceConfigRepository.save(existing);
        configRegistry.updateAliases(existing);
        return existing;
    }
}

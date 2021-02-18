package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.config.ExternalServiceConfig;

import java.util.List;
import java.util.Map;

public interface ExternalServiceConfigService {
    Map<String, String> retrieveAliases(String serviceName);

    List<ExternalServiceConfig> retrieveConfigs();

    ExternalServiceConfig updateConfig(ExternalServiceConfig externalServiceConfig);
}

package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.config.ExternalServiceConfig;

import java.util.List;
import java.util.Map;

public interface ExternalServiceConfigService {
    Map<String, String> retrieveAliases(String serviceName);

    List<ExternalServiceConfig> retrieveConfigs();

    ExternalServiceConfig updateConfig(ExternalServiceConfig externalServiceConfig);
}

package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.ExternalServiceConfig;
import uk.ac.ebi.spot.ontotools.curation.service.ConfigListener;
import uk.ac.ebi.spot.ontotools.curation.service.ConfigRegistry;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.Map;

@Service
public class ConfigRegistryImpl implements ConfigRegistry {

    private Map<String, ConfigListener> configListenerMap;

    @PostConstruct
    public void initialize() {
        this.configListenerMap = new HashMap<>();
    }

    @Override
    public void registerListener(ConfigListener configListener) {
        this.configListenerMap.put(configListener.getName(), configListener);
    }

    @Override
    @Async
    public void updateAliases(ExternalServiceConfig externalServiceConfig) {
        ConfigListener configListener = configListenerMap.get(externalServiceConfig.getName());
        if (configListener != null) {
            configListener.updateAliases(externalServiceConfig.getAliases());
        }
    }
}

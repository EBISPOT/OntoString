package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.ExternalServiceConfig;

public interface ConfigRegistry {
    void registerListener(ConfigListener configListener);

    void updateAliases(ExternalServiceConfig externalServiceConfig);
}

package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.ExternalServiceConfig;

/**
 * Class in charge with keeping a registry of services awaiting for real-time configuration updates.
 */
public interface ConfigRegistry {

    /**
     * Accepts registration requests from services and adds them to the registry.
     */
    void registerListener(ConfigListener configListener);

    /**
     * Accepts new config values to be changed in real-time and pushes them downstream to the corresponding service.
     */
    void updateAliases(ExternalServiceConfig externalServiceConfig);
}

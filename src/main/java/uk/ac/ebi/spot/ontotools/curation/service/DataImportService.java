package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface DataImportService {
    void importData(String fileData, String projectId, String sourceId, User user);
}

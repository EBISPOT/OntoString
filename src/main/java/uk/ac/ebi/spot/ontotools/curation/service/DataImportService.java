package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface DataImportService {
    void importData(String fileData, String projectId, Source source, User user);
}

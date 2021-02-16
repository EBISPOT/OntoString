package uk.ac.ebi.spot.ontotools.curation.service;

import org.springframework.web.multipart.MultipartFile;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

public interface DataImportService {
    void importData(MultipartFile file, String projectId, String sourceId, User user);
}

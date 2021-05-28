package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataPackageDto;

public interface DataImportService {
    void importData(ImportDataPackageDto importDataPackageDto, String projectId, Source source, User user);
}

package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.Source;
import uk.ac.ebi.spot.ontostring.domain.auth.User;
import uk.ac.ebi.spot.ontostring.rest.dto.dataimport.ImportDataPackageDto;

public interface DataImportService {
    void importData(ImportDataPackageDto importDataPackageDto, String projectId, Source source, User user);
}

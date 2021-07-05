package uk.ac.ebi.spot.ontotools.curation.service.impl.dataimport;

import com.opencsv.CSVReader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontotools.curation.constants.CSVImportHeader;
import uk.ac.ebi.spot.ontotools.curation.constants.DataImportFileType;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataPackageDto;
import uk.ac.ebi.spot.ontotools.curation.service.DataImportService;
import uk.ac.ebi.spot.ontotools.curation.util.CSVDataTransform;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component(DataImportFileType.CSV)
public class CSVDataImportAdapter implements DataImportAdapter {

    private static final Logger log = LoggerFactory.getLogger(CSVDataImportAdapter.class);

    @Autowired
    private DataImportService dataImportService;

    @Override
    public String importData(InputStream fileInputStream, String projectId, Source source, User user) {

        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(fileInputStream)));
            List<String[]> data = csvReader.readAll();
            csvReader.close();
            if (!data.isEmpty()) {
                String[] headerLine = data.get(0);
                if (headerLine.length > 0) {
                    Map<String, Integer> headerIndex = new LinkedHashMap<>();

                    for (int i = 0; i < headerLine.length; i++) {
                        String entry = headerLine[i].trim();
                        if (entry.equalsIgnoreCase(CSVImportHeader.TEXT.name())) {
                            headerIndex.put(CSVImportHeader.TEXT.name(), i);
                        }
                        if (entry.equalsIgnoreCase(CSVImportHeader.CONTEXT.name())) {
                            headerIndex.put(CSVImportHeader.CONTEXT.name(), i);
                        }
                        if (entry.equalsIgnoreCase(CSVImportHeader.UPSTREAMID.name())) {
                            headerIndex.put(CSVImportHeader.UPSTREAMID.name(), i);
                        }
                        if (entry.equalsIgnoreCase(CSVImportHeader.PRIORITY.name())) {
                            headerIndex.put(CSVImportHeader.PRIORITY.name(), i);
                        }
                    }

                    if (!headerIndex.containsKey(CSVImportHeader.TEXT.name())) {
                        log.error("Unable to deserialize import data file: Mandatory TEXT header missing.");
                        return "Unable to deserialize import data file: Mandatory TEXT header missing.";
                    }
                    ImportDataPackageDto importDataPackageDto = new CSVDataTransform(data, headerIndex).getImportDataPackageDto();
                    if (importDataPackageDto != null) {
                        dataImportService.importData(importDataPackageDto, projectId, source, user);
                        return null;
                    }

                    log.warn("Unable to deserialize import data file: No data found.");
                    return null;
                }

                log.error("Unable to deserialize import data file: No CSV header found.");
                return "Unable to deserialize import data file: No CSV header found.";
            }

            log.error("Unable to deserialize import data file: No CSV header found.");
            return "Unable to deserialize import data file: No CSV header found.";
        } catch (Exception e) {
            log.error("Unable to deserialize import data file: {}", e.getMessage(), e);
            return "Unable to deserialize import data file: " + e.getMessage();
        }
    }
}

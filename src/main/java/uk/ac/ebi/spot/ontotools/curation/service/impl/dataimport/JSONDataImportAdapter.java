package uk.ac.ebi.spot.ontotools.curation.service.impl.dataimport;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontotools.curation.constants.DataImportFileType;
import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.dataimport.ImportDataPackageDto;
import uk.ac.ebi.spot.ontotools.curation.service.DataImportService;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Component(DataImportFileType.JSON)
public class JSONDataImportAdapter implements DataImportAdapter {

    private static final Logger log = LoggerFactory.getLogger(JSONDataImportAdapter.class);

    @Autowired
    private DataImportService dataImportService;

    private ObjectMapper objectMapper;

    @PostConstruct
    public void initialize() {
        this.objectMapper = new ObjectMapper();
    }

    @Override
    public String importData(InputStream fileInputStream, String projectId, Source source, User user) {

        try {
            String fileData = IOUtils.toString(fileInputStream, StandardCharsets.UTF_8);
            ImportDataPackageDto importDataPackageDto = this.objectMapper.readValue(fileData, new TypeReference<>() {
            });
            dataImportService.importData(importDataPackageDto, projectId, source, user);
            return null;
        } catch (IOException e) {
            log.error("Unable to deserialize import data file: {}", e.getMessage(), e);
            return "Unable to deserialize import data file: " + e.getMessage();
        }
    }
}

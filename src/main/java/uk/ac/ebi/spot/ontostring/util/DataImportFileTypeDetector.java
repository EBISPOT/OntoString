package uk.ac.ebi.spot.ontostring.util;

import com.opencsv.CSVReader;
import uk.ac.ebi.spot.ontostring.constants.DataImportFileType;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class DataImportFileTypeDetector {

    private String fileType;

    public DataImportFileTypeDetector(InputStream fileInputStream) {
        fileType = DataImportFileType.UNKNOWN;
        checkFileType(fileInputStream);
    }

    private void checkFileType(InputStream fileInputStream) {
        try {
            CSVReader csvReader = new CSVReader(new BufferedReader(new InputStreamReader(fileInputStream)));
            String[] header = csvReader.readNext();
            if (header.length > 0) {
                if (header[0].trim().startsWith("{")) {
                    fileType = DataImportFileType.JSON;
                } else {
                    fileType = DataImportFileType.CSV;
                }
            }
        } catch (Exception e) {
        }
    }

    public String getFileType() {
        return fileType;
    }
}

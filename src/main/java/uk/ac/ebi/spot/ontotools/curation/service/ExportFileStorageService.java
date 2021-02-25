package uk.ac.ebi.spot.ontotools.curation.service;

import java.io.InputStream;

public interface ExportFileStorageService {

    String storeFile(InputStream is, String fileName);

    byte[] retrieveFileContent(String fileId);
}

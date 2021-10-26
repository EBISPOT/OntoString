package uk.ac.ebi.spot.ontostring.service.impl;

import com.mongodb.client.gridfs.GridFSBuckets;
import com.mongodb.client.gridfs.GridFSDownloadStream;
import com.mongodb.client.gridfs.model.GridFSFile;
import org.apache.commons.io.IOUtils;
import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.gridfs.GridFsOperations;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontostring.exception.EntityNotFoundException;
import uk.ac.ebi.spot.ontostring.exception.FileProcessingException;
import uk.ac.ebi.spot.ontostring.service.ExportFileStorageService;

import java.io.IOException;
import java.io.InputStream;

@Service
public class ExportFileStorageServiceImpl implements ExportFileStorageService {

    private static final Logger log = LoggerFactory.getLogger(ExportFileStorageService.class);

    @Autowired
    private GridFsOperations gridFsOperations;

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public String storeFile(InputStream is, String fileName) {
        log.info("Storing new file: {}", fileName);
        try {
            ObjectId objectId = is == null ? null : gridFsOperations.store(is, fileName, "");
            log.info("File successfully stored: {}", objectId);
            return objectId != null ? objectId.toString() : null;
        } catch (Exception e) {
            log.error("Encountered exception when saving file [{}]: {}", fileName, e.getMessage(), e);
            return null;
        }
    }

    @Override
    public byte[] retrieveFileContent(String fileId) {
        log.info("Retrieving file content: {}", fileId);
        GridFSFile file = getGridFsdbFileForFileId(fileId);
        byte[] attachmentByteArray;

        try (InputStream inputStream = getFileDownloadStream(file.getObjectId())) {
            attachmentByteArray = IOUtils.toByteArray(inputStream);
        } catch (IOException e) {
            log.error("Unable to get file {}: {}", fileId, e.getMessage(), e);
            throw new FileProcessingException("Unable to get file " + fileId + ": " + e.getMessage());
        }
        log.info("Content for file {} successfully retrieved: {}", fileId, attachmentByteArray.length);

        return attachmentByteArray;
    }

    private GridFSFile getGridFsdbFileForFileId(String fileId) {
        log.info("Received call to get GridFSFile for id: {}", fileId);
        Query query = new Query(Criteria.where("_id").is(fileId));
        GridFSFile file = gridFsOperations.findOne(query);
        if (file == null) {
            log.error("No file found in DB for id: {}", fileId);
            throw new EntityNotFoundException("No file found in DB for id: " + fileId);
        }
        return file;
    }

    private InputStream getFileDownloadStream(ObjectId objectId) {
        log.info("Retrieving file for download: {}", objectId.toString());
        GridFSDownloadStream stream = GridFSBuckets.create(mongoTemplate.getDb())
                .openDownloadStream(objectId);
        log.info("Retrieved download stream {}", objectId.toString());
        return stream;

    }

    private void deleteGridFSFile(String fileId) {
        log.info("Received call to delete GridFSFile with id: {}", fileId);
        Query query = new Query(Criteria.where("_id").is(fileId));
        GridFSFile file = gridFsOperations.findOne(query);
        if (file == null) {
            log.error("No file found in DB for id: {}", fileId);
            throw new EntityNotFoundException("No file found in DB for id: " + fileId);
        }
        gridFsOperations.delete(query);
    }

}

package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Set;

@AllArgsConstructor
@Getter
@Document(collection = "failedImportLogEntries")
public class FailedImportLogEntry {

    @Id
    private String id;

    @Indexed
    private String batchId;

    private String entityName;

    private String context;
}

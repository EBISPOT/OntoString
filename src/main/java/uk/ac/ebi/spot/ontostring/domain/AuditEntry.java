package uk.ac.ebi.spot.ontostring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "auditEntries")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class AuditEntry {

    @Id
    private String id;

    @Indexed
    private String entityId;

    private String action;

    private Provenance provenance;

    private List<MetadataEntry> metadataEntries;

    private DateTime timestamp;
}

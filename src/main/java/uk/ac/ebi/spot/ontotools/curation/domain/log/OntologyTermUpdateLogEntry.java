package uk.ac.ebi.spot.ontotools.curation.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Document(collection = "ontoTermsLogEntries")
public class OntologyTermUpdateLogEntry {

    @Id
    private String id;

    @Indexed
    private String batchId;

    private String ontoTermId;

    private String ontoTermCurie;

    private String ontoTermLabel;

    private String previousStatus;

    private String newStatus;
}

package uk.ac.ebi.spot.ontotools.curation.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Document(collection = "importLogBatches")
@CompoundIndexes({@CompoundIndex(name = "pTime", def = "{'projectId': 1, 'timestamp': 1}")})
public class ImportLogBatch {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private String sourceId;

    private DateTime timestamp;

    private int totalTime;

    private int totalCount;

    private int successful;
}

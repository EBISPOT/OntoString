package uk.ac.ebi.spot.ontostring.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Document(collection = "matchmakingLogBatches")
@CompoundIndexes({@CompoundIndex(name = "pTime", def = "{'projectId': 1, 'timestamp': 1}")})
public class MatchmakingLogBatch {

    @Id
    private String id;

    @Indexed
    private String projectId;

    private DateTime timestamp;
}

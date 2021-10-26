package uk.ac.ebi.spot.ontostring.domain.log;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@AllArgsConstructor
@Getter
@Setter
@Document(collection = "ontoTermsLogBatches")
public class OntologyTermUpdateLogBatch {

    @Id
    private String id;

    private DateTime timestamp;

    private int totalTime;

}

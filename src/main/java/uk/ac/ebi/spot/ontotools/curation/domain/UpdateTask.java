package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.joda.time.DateTime;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "updateTasks")
@AllArgsConstructor
@Getter
public class UpdateTask {

    @Id
    private String id;

    @Indexed
    private String taskType;

    private DateTime started;
}

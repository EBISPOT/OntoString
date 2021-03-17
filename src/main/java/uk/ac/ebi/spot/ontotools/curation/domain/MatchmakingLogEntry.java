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
@Document(collection = "matchmakingLogEntries")
public class MatchmakingLogEntry {

    @Id
    private String id;

    @Indexed
    private String batchId;

    private String entityId;

    private String entityName;

    private List<String> highConfidenceURIs;

    private Set<String> finalURIs;
}

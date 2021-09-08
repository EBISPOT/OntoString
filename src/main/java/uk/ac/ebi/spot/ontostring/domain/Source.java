package uk.ac.ebi.spot.ontostring.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "sources")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@CompoundIndexes({@CompoundIndex(name = "pid_arch", def = "{'projectId': 1, 'archived': 1}"),
        @CompoundIndex(name = "id_pid_arch", def = "{'id': 1, 'projectId': 1, 'archived': 1}")})
public class Source {

    @Id
    private String id;

    private String name;

    private String description;

    private String uri;

    private String type;

    private Provenance created;

    private Provenance lastUpdated;

    @Indexed
    private String projectId;

    private String originalId;

    @Indexed
    private boolean archived;


}

package uk.ac.ebi.spot.ontostring.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document(collection = "ontologyTermContexts")
@CompoundIndexes({@CompoundIndex(name = "mapIdx", def = "{'hasMapping': 1, 'projectId': 1, 'context': 1, 'status': 1}"),
        @CompoundIndex(name = "tcIdx", def = "{'ontologyTermId': 1, 'projectId': 1, 'context': 1}")})
public class OntologyTermContext {

    @Id
    private String id;

    @Indexed
    private String ontologyTermId;

    private String projectId;

    private String context;

    private String status;

    private List<String> mappings;

    private boolean hasMapping;

}

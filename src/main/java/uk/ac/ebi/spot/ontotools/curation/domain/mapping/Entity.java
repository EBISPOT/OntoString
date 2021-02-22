package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

@Document(collection = "entities")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Entity {

    @Id
    private String id;

    private String name;

    private String baseId;

    private String baseField;

    @Indexed
    private String sourceId;

    @Indexed
    private String projectId;

    private Provenance created;

    private EntityStatus mappingStatus;
}

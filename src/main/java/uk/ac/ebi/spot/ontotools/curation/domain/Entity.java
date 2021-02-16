package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import uk.ac.ebi.spot.ontotools.curation.constants.EntityStatus;

@Document(collection = "mappings")
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

    private Provenance created;

    private EntityStatus mappingStatus;
}

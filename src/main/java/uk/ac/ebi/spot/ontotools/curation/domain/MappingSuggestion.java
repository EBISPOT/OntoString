package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "mappingSuggestions")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@CompoundIndexes({@CompoundIndex(name = "eoId", def = "{'entityId': 1, 'ontologyTermId': 1}")})
public class MappingSuggestion {

    @Id
    private String id;

    @Indexed
    private String entityId;

    @Indexed
    private String ontologyTermId;

    private Provenance created;

    @Transient
    private OntologyTerm ontologyTerm;
}

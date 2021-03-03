package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ontologyTerms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OntologyTerm {

    @Id
    private String id;

    private String curie;

    private String iri;

    @Indexed(unique = true)
    private String iriHash;

    private String label;

    private String status;

    private String description;

    private String crossRefs;

    @Override
    public String toString() {
        return curie + " (" + label + ")";
    }
}

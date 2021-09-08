package uk.ac.ebi.spot.ontostring.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collection = "ontologyTerms")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class OntologyTerm {

    @Id
    private String id;

    @Indexed
    private String curie;

    private String iri;

    @Indexed(unique = true)
    private String iriHash;

    private String label;

    private String description;

    private String crossRefs;

    @Indexed
    private List<String> ontoTermContexts;

    @Transient
    private String status;

    @Override
    public String toString() {
        return curie + " (" + label + ")";
    }
}

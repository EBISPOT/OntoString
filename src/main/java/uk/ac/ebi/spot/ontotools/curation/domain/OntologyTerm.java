package uk.ac.ebi.spot.ontotools.curation.domain;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "ontologyTerms")
@Getter
@Setter
public class OntologyTerm {

    @Id
    private String id;

    private String curie;

    private String iri;

    private String label;

    private String status;

    private String description;

    private String crossRefs;

    public OntologyTerm() {

    }

    public OntologyTerm(String curie, String iri, String label, String status, String description, String crossRefs) {
        this.curie = curie;
        this.iri = iri;
        this.label = label;
        this.status = status;
        this.description = description;
        this.crossRefs = crossRefs;
    }
}

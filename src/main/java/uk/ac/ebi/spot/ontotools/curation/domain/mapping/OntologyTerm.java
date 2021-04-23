package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.CompoundIndexes;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;

@Document(collection = "ontologyTerms")
@Getter
@Setter
@CompoundIndexes({@CompoundIndex(name = "mapCon", def = "{'hasMapping': 1, 'contexts': 1}")})
public class OntologyTerm {

    @Id
    private String id;

    @Indexed
    private String curie;

    private String iri;

    @Indexed(unique = true)
    private String iriHash;

    private String label;

    @Indexed
    private List<OntologyTermContext> contexts;

    private String description;

    private String crossRefs;

    private List<String> mappings;

    private boolean hasMapping;

    public OntologyTerm() {
        this.mappings = new ArrayList<>();
        this.hasMapping = false;
    }

    public OntologyTerm(String curie, String iri, String iriHash, String label, List<OntologyTermContext> contexts, String description, String crossRefs) {
        this.curie = curie;
        this.iri = iri;
        this.iriHash = iriHash;
        this.label = label;
        this.contexts = contexts;
        this.description = description;
        this.crossRefs = crossRefs;
        this.mappings = new ArrayList<>();
        this.hasMapping = false;
    }

    public void addMapping(String mappingId) {
        if (!this.mappings.contains(mappingId)) {
            this.mappings.add(mappingId);
        }
        this.hasMapping = true;
    }

    public void removeMapping(String mappingId) {
        if (this.mappings.contains(mappingId)) {
            this.mappings.remove(mappingId);
            if (this.mappings.isEmpty()) {
                this.hasMapping = false;
            }
        }
    }

    @Override
    public String toString() {
        return curie + " (" + label + ")";
    }
}

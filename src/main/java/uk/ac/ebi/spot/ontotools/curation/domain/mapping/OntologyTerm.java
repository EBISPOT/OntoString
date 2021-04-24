package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
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

    @Indexed
    private List<OntologyTermContext> contexts;

    private String description;

    private String crossRefs;

    public void addMapping(Mapping mapping) {
        if (contexts == null) {
            return;
        }

        OntologyTermContext found = null;
        int index = -1;
        for (int i = 0; i < contexts.size(); i++) {
            OntologyTermContext ontologyTermContext = contexts.get(i);
            if (ontologyTermContext.getProjectId().equals(mapping.getProjectId()) && ontologyTermContext.getContext().equals(mapping.getContext())) {
                found = ontologyTermContext;
                index = i;
                break;
            }
        }
        if (found != null) {
            if (!found.getMappings().contains(mapping.getId())) {
                found.getMappings().add(mapping.getId());
            }
            found.setHasMapping(true);
            contexts.set(index, found);
        }
    }

    public void removeMapping(Mapping mapping) {
        if (contexts == null) {
            return;
        }
        OntologyTermContext found = null;
        int index = -1;
        for (int i = 0; i < contexts.size(); i++) {
            OntologyTermContext ontologyTermContext = contexts.get(i);
            if (ontologyTermContext.getProjectId().equals(mapping.getProjectId()) && ontologyTermContext.getContext().equals(mapping.getContext())) {
                found = ontologyTermContext;
                index = i;
                break;
            }
        }
        if (found != null) {
            if (found.getMappings().contains(mapping.getId())) {
                found.getMappings().remove(mapping.getId());
            }
            if (found.getMappings().isEmpty()) {
                found.setHasMapping(false);
            }
            contexts.set(index, found);
        }
    }

    @Override
    public String toString() {
        return curie + " (" + label + ")";
    }
}

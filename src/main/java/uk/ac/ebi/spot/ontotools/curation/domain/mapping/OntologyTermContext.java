package uk.ac.ebi.spot.ontotools.curation.domain.mapping;

import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class OntologyTermContext {

    private String projectId;

    private String context;

    private String status;

    private List<String> mappings;

    private boolean hasMapping;

    public OntologyTermContext() {
        this.mappings = new ArrayList<>();
        this.hasMapping = false;
    }

    public OntologyTermContext(String projectId, String context, String status) {
        this.projectId = projectId;
        this.context = context;
        this.status = status;
        this.mappings = new ArrayList<>();
        this.hasMapping = false;
    }
}

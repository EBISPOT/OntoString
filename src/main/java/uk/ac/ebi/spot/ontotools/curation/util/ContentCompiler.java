package uk.ac.ebi.spot.ontotools.curation.util;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.ArrayList;
import java.util.List;

public class ContentCompiler {

    private List<String> lines;

    public ContentCompiler() {
        this.lines = new ArrayList<>();
        this.lines.add(new StringBuffer()
                .append("IRI")
                .append(",")
                .append("Label")
                .append(",")
                .append("Cross-refs")
                .append(",")
                .append("Mappings")
                .toString());
    }

    public void addOntologyTerm(OntologyTerm ontologyTerm, String entityNames) {
        StringBuffer sb = new StringBuffer();
        sb.append(ontologyTerm.getIri())
                .append(",")
                .append("\"" + ontologyTerm.getLabel() + "\"")
                .append(",");
        if (ontologyTerm.getCrossRefs() != null && !ontologyTerm.getCrossRefs().equalsIgnoreCase("")) {
            sb.append(ontologyTerm.getCrossRefs());
        }
        sb.append(",").append(entityNames);
        lines.add(sb.toString());
    }

    public String getContent() {
        return StringUtils.join(this.lines, "\n");
    }
}

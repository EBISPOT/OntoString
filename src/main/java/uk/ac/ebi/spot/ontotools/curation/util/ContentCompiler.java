package uk.ac.ebi.spot.ontotools.curation.util;

import org.apache.commons.lang3.StringUtils;
import org.eclipse.jetty.util.StringUtil;
import uk.ac.ebi.spot.ontotools.curation.domain.mapping.OntologyTerm;

import java.util.ArrayList;
import java.util.List;

public class ContentCompiler {

    private List<String> lines;

    public ContentCompiler() {
        this.lines = new ArrayList<>();
    }

    public void addOntologyTerm(OntologyTerm ontologyTerm) {
        StringBuffer sb = new StringBuffer();
        sb.append(ontologyTerm.getIri()).append(",").append("\"" + ontologyTerm.getLabel() + "\"");
        if (ontologyTerm.getCrossRefs() != null && !ontologyTerm.getCrossRefs().equalsIgnoreCase("")) {
            sb.append(",").append(ontologyTerm.getCrossRefs());
        }
        lines.add(sb.toString());
    }

    public String getContent() {
        return StringUtils.join(this.lines, "\n");
    }
}

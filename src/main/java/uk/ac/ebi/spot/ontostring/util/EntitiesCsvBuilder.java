package uk.ac.ebi.spot.ontostring.util;

import org.apache.commons.lang3.StringUtils;
import uk.ac.ebi.spot.ontostring.domain.mapping.Entity;
import uk.ac.ebi.spot.ontostring.domain.mapping.OntologyTerm;

import java.util.ArrayList;
import java.util.List;

public class EntitiesCsvBuilder {

    private List<String> lines;

    // <entityid> <upstreamId>< <priority> <text> <context> <MAPPING|SUGGESTION> <term>
    public EntitiesCsvBuilder() {
        this.lines = new ArrayList<>();
        this.lines.add(new StringBuffer()
                .append("entityId")
                .append(",")
                .append("context")
                .append(",")
                .append("text")
                .append(",")
                .append("type")
                .append(",")
                .append("term")
                .append(",")
                .append("iri")
                .toString());
    }

    public void addSuggestionRow(Entity entity, OntologyTerm ontologyTerm) {
        StringBuffer sb = new StringBuffer();
        sb
                .append("\"" + entity.getId() + "\"")
                .append(",")
                .append("\"" + entity.getContext() + "\"")
                .append(",")
                .append("\"" + entity.getName() + "\"")
                .append(",")
                .append("\"SUGGESTION\"")
                .append(",")
                .append("\"" + ontologyTerm.getLabel() + "\"")
                .append(",")
                .append(ontologyTerm.getIri());
        lines.add(sb.toString());
    }

    public void addMappingRow(Entity entity, OntologyTerm ontologyTerm) {
        StringBuffer sb = new StringBuffer();
        sb
                .append("\"" + entity.getId() + "\"")
                .append(",")
                .append("\"" + entity.getContext() + "\"")
                .append(",")
                .append("\"" + entity.getName() + "\"")
                .append(",")
                .append("\"MAPPING\"")
                .append(",")
                .append("\"" + ontologyTerm.getLabel() + "\"")
                .append(",")
                .append(ontologyTerm.getIri());
        lines.add(sb.toString());
    }

    public String getContent() {
        return StringUtils.join(this.lines, "\n");
    }
}

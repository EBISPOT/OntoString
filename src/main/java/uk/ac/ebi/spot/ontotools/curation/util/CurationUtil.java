package uk.ac.ebi.spot.ontotools.curation.util;

import java.util.ArrayList;
import java.util.List;

public class CurationUtil {

    public static List<String> sToList(String s) {
        List<String> list = new ArrayList<>();
        if (s == null) {
            return list;
        }

        String[] parts = s.split(",");
        for (String part : parts) {
            part = part.trim();
            if (!"".equals(part)) {
                list.add(part);
            }
        }
        return list;
    }

    public static String ontoFromIRI(String iri) {
        int index = iri.lastIndexOf("/");
        String rest = iri.substring(index + 1);
        index = rest.indexOf("_");
        if (index == -1) {
            return iri;
        }
        return rest.substring(0, index);
    }

}

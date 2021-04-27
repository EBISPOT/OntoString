package uk.ac.ebi.spot.ontotools.curation.constants;

import org.apache.commons.lang3.StringUtils;

import java.util.List;

public class RestInteractionConstants {

    public static final String ZOOMA_PROPERTY_VALUE = "propertyValue";

    public static final String ZOOMA_FILTER = "filter";

    public static final String ZOOMA_FILTER_VALUE_REQUIRED = "required";

    public static final String ZOOMA_FILTER_VALUE_ONTOLOGIES = "ontologies";

    public static final String OLS_TERMS = "/terms";

    public static final String OLS_ANCESTORS = "/ancestors";

    public static final String OLS_PARENTS = "/parents";

    public static final String OLS_IDTYPE_IRI = "iri";

    public static final String OLS_IDTYPE_OBOID = "obo_id";

    public static final String OLS_PAGE = "page";

    public static final String OLS_PARAM_Q = "q";

    public static final String OLS_PARAM_ONTOLOGY = "ontology";

    public static final String OLS_PARAM_ALLCHILDRENOF = "allChildrenOf";

    public static final String OLS_PARAM_CHILDRENOF = "childrenOf";

    public static String zoomaFilterValueFromList(List<String> datasources, List<String> ontologies) {
        if (datasources != null && !datasources.isEmpty()) {
            return ZOOMA_FILTER_VALUE_REQUIRED + ":[" + StringUtils.join(datasources, ",") + "]";
        }
        if (ontologies != null && !ontologies.isEmpty()) {
            return ZOOMA_FILTER_VALUE_REQUIRED + ":[none]," + ZOOMA_FILTER_VALUE_ONTOLOGIES + ":[" + StringUtils.join(ontologies, ",") + "]";
        }
        return "";
    }
}

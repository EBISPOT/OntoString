package uk.ac.ebi.spot.ontotools.curation.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontotools.curation.util.HttpEntityBuilder;

@Component
public class RestInteractionConfig {

    @Value("${ontotools.zooma.base}")
    private String zoomaBase;

    @Value("${ontotools.zooma.endpoints.annotate}")
    private String zoomaAnnotateEndpoint;

    @Value("${ontotools.oxo.base}")
    private String oxoBase;

    @Value("${ontotools.oxo.mapping-distance}")
    private int oxoMappingDistance;

    @Value("${ontotools.ols.base}")
    private String olsBase;

    @Value("${ontotools.ols.endpoints.ontologies}")
    private String olsOntologiesEndpoint;

    @Value("${server.name}")
    private String serverName;

    public HttpEntityBuilder httpEntity() {
        return new HttpEntityBuilder(serverName);
    }

    public String getZoomaBase() {
        return zoomaBase;
    }

    public String getZoomaAnnotateEndpoint() {
        return zoomaBase + zoomaAnnotateEndpoint;
    }

    public String getOxoBase() {
        return oxoBase;
    }

    public int getOxoMappingDistance() {
        return oxoMappingDistance;
    }

    public String getOlsBase() {
        return olsBase;
    }

    public String getOlsOntologiesEndpoint() {
        return olsBase + olsOntologiesEndpoint;
    }
}

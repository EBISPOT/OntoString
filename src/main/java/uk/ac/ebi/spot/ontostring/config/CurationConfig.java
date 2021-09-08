package uk.ac.ebi.spot.ontostring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;

import java.util.List;

@Component
public class CurationConfig {

    @Value("${ontotools-curation.auth.enabled}")
    private boolean authEnabled;

    @Value("${ontotools-curation.auth.cert:#{NULL}}")
    private String certPath;

    @Value("${ontotools-curation.auth.curators.auth-mechanism:JWT_DOMAIN}")
    private String curatorAuthMechanism;

    @Value("${ontotools-curation.auth.curators.jwt-domains:#{NULL}}")
    private String curatorDomains;

    @Value("${ontotools-curation.db:#{NULL}}")
    private String dbName;

    public String getDbName() {
        return dbName;
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getCuratorAuthMechanism() {
        return curatorAuthMechanism;
    }

    public List<String> getCuratorDomains() {
        return CurationUtil.sToList(curatorDomains);
    }
}

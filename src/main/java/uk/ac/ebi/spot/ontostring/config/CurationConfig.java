package uk.ac.ebi.spot.ontostring.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;

import java.util.List;

@Component
public class CurationConfig {

    @Value("${ontostring.auth.enabled}")
    private boolean authEnabled;

    @Value("${ontostring.auth.cert:#{NULL}}")
    private String certPath;

    @Value("${ontostring.auth.curators.auth-mechanism:JWT_DOMAIN}")
    private String curatorAuthMechanism;

    @Value("${ontostring.auth.curators.jwt-domains:#{NULL}}")
    private String curatorDomains;

    @Value("${ontostring.db:#{NULL}}")
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

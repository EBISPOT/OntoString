package uk.ac.ebi.spot.ontostring.system;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SystemConfigProperties {

    @Value("${server.name}")
    private String serverName;

    @Value("${server.port}")
    private String serverPort;

    @Value("${spring.data.mongodb.uri}")
    private String mongoUri;

    @Value("${ontostring.auth.enabled}")
    private boolean authEnabled;

    @Value("${ontostring.auth.cert:#{NULL}}")
    private String certPath;

    @Value("${ontostring.auth.unauthenticated-endpoints-prefix:#{NULL}}")
    private String unauthenticatedEndpointsPrefix;

    @Value("${ontostring.admin.robot-user}")
    private String robotUser;

    public String getServerName() {
        return serverName;
    }

    public String getServerPort() {
        return serverPort;
    }

    public String getMongoUri() {
        return mongoUri;
    }

    public String getDbUser() {
        return System.getenv(GeneralCommon.DB_USER);
    }

    public String getDbPassword() {
        return System.getenv(GeneralCommon.DB_PASSWORD);
    }

    public boolean isAuthEnabled() {
        return authEnabled;
    }

    public String getCertPath() {
        return certPath;
    }

    public String getRobotUser() {
        return robotUser;
    }

    public String getUnauthenticatedEndpointsPrefix() {
        return unauthenticatedEndpointsPrefix;
    }
}

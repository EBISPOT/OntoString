package uk.ac.ebi.spot.ontotools.curation.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;
import org.springframework.data.mongodb.gridfs.GridFsTemplate;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import uk.ac.ebi.spot.ontotools.curation.system.SystemConfigProperties;

public class MongoConfig {

    @Configuration
    @EnableMongoRepositories(basePackages = {"uk.ac.ebi.spot.ontotools.curation.repository"})
    @EnableTransactionManagement
    @Profile({"sandbox"})
    public static class MongoConfigDevSandbox extends AbstractMongoClientConfiguration {

        @Autowired
        private SystemConfigProperties systemConfigProperties;

        @Autowired
        private CurationConfig curationConfig;

        @Autowired
        private MappingMongoConverter mongoConverter;

        @Override
        protected String getDatabaseName() {
            return curationConfig.getDbName();
        }

        @Bean
        public GridFsTemplate gridFsTemplate() {
            return new GridFsTemplate(mongoDbFactory(), mongoConverter);
        }

        @Override
        public MongoClient mongoClient() {
            return MongoClients.create("mongodb://" + systemConfigProperties.getMongoUri());
        }
    }

    @Configuration
    @EnableMongoRepositories(basePackages = {"uk.ac.ebi.spot.ontotools.curation.repository"})
    @EnableTransactionManagement
    @Profile({"prod"})
    public static class MongoConfigProd extends AbstractMongoClientConfiguration {

        @Autowired
        private SystemConfigProperties systemConfigProperties;

        @Autowired
        private CurationConfig curationConfig;

        @Autowired
        private MappingMongoConverter mongoConverter;

        @Override
        protected String getDatabaseName() {
            return curationConfig.getDbName();
        }

        @Bean
        public GridFsTemplate gridFsTemplate() {
            return new GridFsTemplate(mongoDbFactory(), mongoConverter);
        }

        @Override
        public MongoClient mongoClient() {
            String dbUser = systemConfigProperties.getDbUser();
            String dbPassword = systemConfigProperties.getDbPassword();
            String credentials = "";
            if (dbUser != null && dbPassword != null) {
                dbUser = dbUser.trim();
                dbPassword = dbPassword.trim();
                if (!dbUser.equalsIgnoreCase("") &&
                        !dbPassword.equalsIgnoreCase("")) {
                    credentials = dbUser + ":" + dbPassword + "@";
                }
            }

            return MongoClients.create("mongodb://" + credentials + systemConfigProperties.getMongoUri());
        }
    }
}

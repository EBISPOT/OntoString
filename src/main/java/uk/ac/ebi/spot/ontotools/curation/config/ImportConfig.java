package uk.ac.ebi.spot.ontotools.curation.config;

import org.springframework.beans.factory.config.ServiceLocatorFactoryBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uk.ac.ebi.spot.ontotools.curation.service.impl.dataimport.DataImportFactory;

@Configuration
public class ImportConfig {

    @Bean
    public ServiceLocatorFactoryBean itemDataImportFactoryBean() {
        ServiceLocatorFactoryBean bean = new ServiceLocatorFactoryBean();
        bean.setServiceLocatorInterface(DataImportFactory.class);
        return bean;
    }

    @Bean
    public DataImportFactory dataImportFactory() {
        return (DataImportFactory) itemDataImportFactoryBean().getObject();
    }

}

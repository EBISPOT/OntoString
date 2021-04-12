package uk.ac.ebi.spot.ontotools.curation;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

public class ApplicationWebXml extends SpringBootServletInitializer {

    private static final Logger log = LoggerFactory.getLogger(ApplicationWebXml.class);

    @Override
    protected SpringApplicationBuilder configure(
            SpringApplicationBuilder application) {
        return application.profiles(addDefaultProfile()).sources(Application.class);
    }

    private String addDefaultProfile() {
        String profile = System.getProperty("spring.profiles.active");
        if (profile != null) {
            log.info("Running with Spring profile(s) : {}", profile);
            return profile;
        }

        log.warn("No Spring profile configured, running with default configuration");
        return "dev";
    }

}

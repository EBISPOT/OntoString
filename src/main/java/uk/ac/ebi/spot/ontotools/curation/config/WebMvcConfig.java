package uk.ac.ebi.spot.ontotools.curation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.multipart.support.MultipartFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;


public class WebMvcConfig {

    @Configuration
    public static class GeneralWebMvcConfig implements WebMvcConfigurer {

        @Bean
        public AuthInterceptor authInterceptor() {
            return new AuthInterceptor();
        }

        @Override
        public void addInterceptors(InterceptorRegistry registry) {
            registry.addInterceptor(authInterceptor());
        }

        @Bean
        public Executor taskExecutor() {
            return new SimpleAsyncTaskExecutor();
        }

        @Bean
        @Order(0)
        public MultipartFilter multipartFilter() {
            MultipartFilter multipartFilter = new MultipartFilter();
            multipartFilter.setMultipartResolverBeanName("filterMultipartResolver");
            return multipartFilter;
        }

        @Bean(name = "filterMultipartResolver")
        public CommonsMultipartResolver multipartResolver() {
            CommonsMultipartResolver multipartResolver = new CommonsMultipartResolver();
            multipartResolver.setMaxUploadSize(30000000);
            return multipartResolver;
        }

        @Override
        public void addCorsMappings(CorsRegistry registry) {
            registry.addMapping("/**")
                    .allowedOrigins("http://localhost:80")
                    .allowedMethods("GET", "POST", "PUT", "OPTIONS", "DELETE", "PATCH", "FETCH")
                    .allowCredentials(true)
                    .allowedHeaders("*", "Access-Control-Allow-Origin", "Access-Control-Allow-Credentials");
        }
    }
}
package uk.ac.ebi.spot.ontotools.curation.config;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

@Configuration
public class RestTemplateConfig {

    @Bean
    public RestTemplate restTemplate() {
        CloseableHttpClient httpClient =
                HttpClients.custom().setSSLHostnameVerifier(new NoopHostnameVerifier()).build();
        HttpComponentsClientHttpRequestFactory requestFactory =
                new HttpComponentsClientHttpRequestFactory();
        requestFactory.setHttpClient(httpClient);
        requestFactory.setReadTimeout(40000);
        requestFactory.setConnectTimeout(40000);
        requestFactory.setConnectionRequestTimeout(40000);
        return new RestTemplate(requestFactory);
    }

}

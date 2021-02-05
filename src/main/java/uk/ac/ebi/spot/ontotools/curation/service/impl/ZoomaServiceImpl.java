package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;
import uk.ac.ebi.spot.ontotools.curation.config.RestInteractionConfig;
import uk.ac.ebi.spot.ontotools.curation.constants.RestInteractionConstants;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.ZoomaService;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
public class ZoomaServiceImpl implements ZoomaService {

    private static final Logger log = LoggerFactory.getLogger(ZoomaService.class);

    @Autowired
    private RestInteractionConfig restInteractionConfig;

    @Autowired
    private RestTemplate restTemplate;

    public List<ZoomaResponseDto> annotate(String entityValue, List<String> datasources, List<String> ontologies) {
        log.info("Calling Zooma for entity value: {}", entityValue);
        String encodedString;
        try {
            encodedString = URLEncoder.encode(entityValue, StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            log.error("Unable to encode string: {} - {}", entityValue, e.getMessage(), e);
            return new ArrayList<>();
        }
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(restInteractionConfig.getZoomaAnnotateEndpoint())
                .queryParam(RestInteractionConstants.ZOOMA_PROPERTY_VALUE, encodedString)
                .queryParam(RestInteractionConstants.ZOOMA_FILTER, RestInteractionConstants.zoomaFilterValueFromList(datasources, ontologies));
        String endpoint = uriBuilder.build().toUriString();

        try {
            HttpEntity httpEntity = restInteractionConfig.httpEntity().build();
            ResponseEntity<List<ZoomaResponseDto>> response =
                    restTemplate.exchange(endpoint,
                            HttpMethod.GET, httpEntity,
                            new ParameterizedTypeReference<List<ZoomaResponseDto>>() {
                            });

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                log.info("[{}] Zooma: received {} suggestions.", entityValue, response.getBody().size());
                return response.getBody();
            }
        } catch (Exception e) {
            log.error("Unable to call Zooma: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

}

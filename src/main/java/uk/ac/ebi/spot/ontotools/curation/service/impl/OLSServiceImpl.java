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
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSResponseDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;

import java.util.ArrayList;
import java.util.List;

@Service
public class OLSServiceImpl implements OLSService {

    private static final Logger log = LoggerFactory.getLogger(OLSService.class);

    @Autowired
    private RestInteractionConfig restInteractionConfig;

    @Autowired
    private RestTemplate restTemplate;

    public List<OLSTermDto> retrieveTerms(String ontologyId, String identifierValue) {
        log.info("Calling OLS: {} - {}", ontologyId, identifierValue);
        String base = restInteractionConfig.getOlsOntologiesEndpoint() + "/" + ontologyId + RestInteractionConstants.OLS_TERMS;
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(base)
                .queryParam(RestInteractionConstants.OLS_IDTYPE_IRI, identifierValue);
        String endpoint = uriBuilder.build().toUriString();

        try {
            HttpEntity httpEntity = restInteractionConfig.httpEntity().build();
            ResponseEntity<OLSResponseDto> response =
                    restTemplate.exchange(endpoint,
                            HttpMethod.GET, httpEntity,
                            new ParameterizedTypeReference<OLSResponseDto>() {
                            });

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                log.info("[{}] OLS: received {} terms.", identifierValue, response.getBody().getEmbedded().getTerms().size());
                return response.getBody().getEmbedded().getTerms();
            }
            if (response.getStatusCode().equals(HttpStatus.NOT_FOUND)) {
                log.info("[{}] OLS: Term not found.", identifierValue);
            }
        } catch (Exception e) {
            log.error("Unable to call OLS: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

}

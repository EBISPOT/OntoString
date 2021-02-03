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
import uk.ac.ebi.spot.ontotools.curation.config.RestInteractionConfig;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXOMappingResponseDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXORequestDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXOResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.service.OXOService;

import java.util.ArrayList;
import java.util.List;

@Service
public class OXOServiceImpl implements OXOService {


    private static final Logger log = LoggerFactory.getLogger(OLSService.class);

    @Autowired
    private RestInteractionConfig restInteractionConfig;

    @Autowired
    private RestTemplate restTemplate;

    public List<OXOMappingResponseDto> findMapping(List<String> ids, List<String> ontologies) {
        log.info("Calling OXO: {} - {}", ids, ontologies);

        try {
            HttpEntity httpEntity = restInteractionConfig.httpEntity()
                    .withJsonBody(new OXORequestDto(ids, ontologies, restInteractionConfig.getOxoMappingDistance()))
                    .build();
            ResponseEntity<OXOResponseDto> response =
                    restTemplate.exchange(restInteractionConfig.getOxoBase(),
                            HttpMethod.POST, httpEntity,
                            new ParameterizedTypeReference<OXOResponseDto>() {
                            });

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                log.info("[{}] OXO: received {} terms.", ids, response.getBody().getEmbedded().getSearchResults().size());
                if (!response.getBody().getEmbedded().getSearchResults().isEmpty() &&
                        response.getBody().getEmbedded().getSearchResults().get(0).getMappingResponse() != null) {
                    return response.getBody().getEmbedded().getSearchResults().get(0).getMappingResponse();
                }
            }
        } catch (Exception e) {
            log.error("Unable to call OXO: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }
}

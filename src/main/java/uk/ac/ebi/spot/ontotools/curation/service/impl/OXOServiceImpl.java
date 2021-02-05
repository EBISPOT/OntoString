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
import uk.ac.ebi.spot.ontotools.curation.service.*;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OXOServiceImpl implements OXOService, ConfigListener {

    private static final String SERVICE_NAME = "OXO";

    private static final Logger log = LoggerFactory.getLogger(OLSService.class);

    @Autowired
    private RestInteractionConfig restInteractionConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExternalServiceConfigService externalServiceConfigService;

    @Autowired
    private ConfigRegistry configRegistry;

    private Map<String, String> ontoAliases;

    @PostConstruct
    public void initialize() {
        configRegistry.registerListener(this);
        this.ontoAliases = externalServiceConfigService.retrieveAliases(SERVICE_NAME);
    }

    public List<OXOMappingResponseDto> findMapping(List<String> ids, List<String> projectOntologies) {
        List<String> ontologies = this.fixAliases(projectOntologies);
        log.info("Calling OXO: {} - {} | {}", ids, projectOntologies, ontologies);
        log.info(" -- ");
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

    private List<String> fixAliases(List<String> ontologies) {
        List<String> result = new ArrayList<>();
        for (String ontology : ontologies) {
            ontology = ontology.toLowerCase();
            if (ontoAliases.containsKey(ontology)) {
                if (!result.contains(ontoAliases.get(ontology))) {
                    result.add(ontoAliases.get(ontology));
                }
            } else {
                result.add(ontoAliases.get(ontology));
            }
        }
        return result;
    }

    @Override
    public void updateAliases(List<String> aliases) {
        this.ontoAliases.putAll(CurationUtil.parseAliases(aliases));
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
    }
}

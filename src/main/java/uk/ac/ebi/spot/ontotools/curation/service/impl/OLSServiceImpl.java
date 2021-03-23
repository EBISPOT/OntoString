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
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryResponseDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSResponseDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontotools.curation.service.ConfigListener;
import uk.ac.ebi.spot.ontotools.curation.service.ConfigRegistry;
import uk.ac.ebi.spot.ontotools.curation.service.ExternalServiceConfigService;
import uk.ac.ebi.spot.ontotools.curation.service.OLSService;
import uk.ac.ebi.spot.ontotools.curation.util.CurationUtil;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class OLSServiceImpl implements OLSService, ConfigListener {

    private static final Logger log = LoggerFactory.getLogger(OLSService.class);

    private static final String SERVICE_NAME = "OLS";

    @Autowired
    private RestInteractionConfig restInteractionConfig;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ExternalServiceConfigService externalServiceConfigService;

    @Autowired
    private ConfigRegistry configRegistry;

    /**
     * Map to keep alias correspondences to ensure namespace acceptance by the service.
     * For example, Orphanet to ORDO (since sometimes data retrieved from OXO uses the Orphanet namespace)
     */
    private Map<String, String> ontoAliases;

    @PostConstruct
    public void initialize() {
        /**
         * Register this service as a listener for real-time configuration updates.
         * Retrieve config data from DB.
         */
        configRegistry.registerListener(this);
        this.ontoAliases = externalServiceConfigService.retrieveAliases(SERVICE_NAME);
    }

    public List<OLSTermDto> retrieveTerms(String ontologyId, String identifierValue) {
        log.info("Calling OLS: {} - {}", ontologyId, identifierValue);
        if (ontoAliases.containsKey(ontologyId.toLowerCase())) {
            log.info("Replacing ontologyId [{}] with alias: {}", ontologyId, ontoAliases.get(ontologyId.toLowerCase()));
            ontologyId = ontoAliases.get(ontologyId.toLowerCase());
        }
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

    @Override
    public List<OLSQueryDocDto> query(String prefix) {
        log.info("Calling OLS search: {}", prefix);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(restInteractionConfig.getOlsSearchEndpoint())
                .queryParam(RestInteractionConstants.OLS_PARAM_Q, prefix);
        String endpoint = uriBuilder.build().toUriString();

        try {
            HttpEntity httpEntity = restInteractionConfig.httpEntity().build();
            ResponseEntity<OLSQueryResponseDto> response =
                    restTemplate.exchange(endpoint,
                            HttpMethod.GET, httpEntity,
                            new ParameterizedTypeReference<>() {
                            });

            if (response.getStatusCode().equals(HttpStatus.OK)) {
                log.info("OLS query [{}]: received {} docs.", prefix, response.getBody().getResponse().getDocs().size());
                return response.getBody().getResponse().getDocs();
            }
        } catch (Exception e) {
            log.error("Unable to call OLS: {}", e.getMessage(), e);
        }

        return new ArrayList<>();
    }

    /**
     * TODO: Implement
     * Scheduled task to periodically go through all local terms with status CURRENT | AWAITING_IMPORT or NEEDS_IMPORT
     * and repeat the process associated with checking the status - as per the initial term creation
     * <p>
     * Why??
     * <p>
     * Do we have to introduce project-based terms? If not - how do we use project preferences to do this import?
     */
    public void importOLS() {

    }

    @Override
    public void updateAliases(List<String> aliases) {
        /**
         * Call received in real-time from the ConfigRegistry to update aliases.
         */
        this.ontoAliases.putAll(CurationUtil.parseAliases(aliases));
    }

    @Override
    public String getName() {
        return SERVICE_NAME;
    }
}

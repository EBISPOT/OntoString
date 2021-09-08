package uk.ac.ebi.spot.ontostring.service.impl;

import org.apache.commons.lang3.StringUtils;
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
import uk.ac.ebi.spot.ontostring.config.RestInteractionConfig;
import uk.ac.ebi.spot.ontostring.constants.RestInteractionConstants;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.domain.ProjectContext;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSQueryResponseDto;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSResponseDto;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontostring.service.ConfigListener;
import uk.ac.ebi.spot.ontostring.service.ConfigRegistry;
import uk.ac.ebi.spot.ontostring.service.ExternalServiceConfigService;
import uk.ac.ebi.spot.ontostring.service.OLSService;
import uk.ac.ebi.spot.ontostring.util.CurationUtil;

import javax.annotation.PostConstruct;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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

    @Override
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
            OLSResponseDto olsResponseDto = this.callOLS(endpoint);
            if (olsResponseDto != null) {
                log.info("[{}] OLS: received {} terms.", identifierValue, olsResponseDto.getEmbedded().getTerms().size());
                return olsResponseDto.getEmbedded().getTerms();
            }
            log.info("[{}] OLS: Term not found.", identifierValue);
        } catch (Exception e) {
            log.error("Unable to call OLS: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    @Override
    public List<OLSQueryDocDto> query(String prefix, Project project, String context, boolean usePreferred, boolean useGraphRestrictions) {
        log.info("Calling OLS search: {}", prefix);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(restInteractionConfig.getOlsSearchEndpoint())
                .queryParam(RestInteractionConstants.OLS_PARAM_Q, prefix);
        ProjectContext found = null;
        for (ProjectContext projectContext : project.getContexts()) {
            if (projectContext.getName().equalsIgnoreCase(context)) {
                found = projectContext;
                break;
            }
        }

        if (usePreferred && found != null) {
            String ontoParam = "";
            if (found.getOntologies() != null) {
                ontoParam = StringUtils.join(found.getOntologies(), ",").toLowerCase();
            }
            if (!"".equalsIgnoreCase(ontoParam)) {
                uriBuilder = uriBuilder.queryParam(RestInteractionConstants.OLS_PARAM_ONTOLOGY, ontoParam);
            }
        }

        if (useGraphRestrictions && found != null && found.getProjectContextGraphRestriction() != null) {
            String qProperty = found.getProjectContextGraphRestriction().getDirect() ? RestInteractionConstants.OLS_PARAM_CHILDRENOF : RestInteractionConstants.OLS_PARAM_ALLCHILDRENOF;
            uriBuilder = uriBuilder.queryParam(qProperty, StringUtils.join(found.getProjectContextGraphRestriction().getIris(), ","));
        }

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

    @Override
    public OLSTermDto retrieveOriginalTerm(String termId, boolean retrieveByIRI) {
        log.info("Calling OLS terms: {}", termId);
        UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(restInteractionConfig.getOlsTermsEndpoint());
        if (retrieveByIRI) {
            uriBuilder = uriBuilder.queryParam(RestInteractionConstants.OLS_IDTYPE_IRI, termId);
        } else {
            uriBuilder = uriBuilder.queryParam(RestInteractionConstants.OLS_IDTYPE_OBOID, termId);
        }
        String endpoint = uriBuilder.build().toUriString();

        try {
            OLSResponseDto olsResponseDto = this.callOLS(endpoint);
            if (olsResponseDto != null) {
                List<OLSTermDto> olsTermDtos = olsResponseDto.getEmbedded().getTerms();
                log.info("OLS terms [{}]: received {} docs.", termId, olsTermDtos.size());
                for (OLSTermDto olsTermDto : olsTermDtos) {
                    if (olsTermDto.getDefiningOntology() != null && olsTermDto.getDefiningOntology()) {
                        return olsTermDto;
                    }
                }

            }
        } catch (Exception e) {
            log.error("Unable to call OLS: {}", e.getMessage(), e);
        }
        return null;
    }

    @Override
    public List<OLSTermDto> retrieveAncestors(String ontologyId, String iri, boolean direct) {
        try {
            String suffix = direct ? RestInteractionConstants.OLS_PARENTS : RestInteractionConstants.OLS_ANCESTORS;
            String endpoint = restInteractionConfig.getOlsOntologiesEndpoint() + "/" + ontologyId + RestInteractionConstants.OLS_TERMS +
                    "/" + URLEncoder.encode(iri, StandardCharsets.UTF_8.name()) + suffix;

            List<OLSTermDto> completeList = new ArrayList<>();
            OLSResponseDto olsResponseDto = this.callOLS(endpoint);
            if (olsResponseDto != null) {
                completeList.addAll(olsResponseDto.getEmbedded().getTerms());
                int totalPages = olsResponseDto.getPage().getTotalPages() - 1;
                int currentPage = 1;

                while (totalPages > 0) {
                    UriComponentsBuilder uriBuilder = UriComponentsBuilder.fromHttpUrl(endpoint)
                            .queryParam(RestInteractionConstants.OLS_PAGE, currentPage);
                    String newEndpoint = uriBuilder.build().toUriString();

                    olsResponseDto = this.callOLS(newEndpoint);
                    if (olsResponseDto != null) {
                        completeList.addAll(olsResponseDto.getEmbedded().getTerms());
                        currentPage++;
                        totalPages--;
                    } else {
                        break;
                    }
                }
            }

            return completeList;
        } catch (Exception e) {
            log.error("Unable to call OLS: {}", e.getMessage(), e);
        }
        return new ArrayList<>();
    }

    private OLSResponseDto callOLS(String endpoint) {
        HttpEntity httpEntity = restInteractionConfig.httpEntity().build();
        ResponseEntity<OLSResponseDto> response =
                restTemplate.exchange(endpoint,
                        HttpMethod.GET, httpEntity,
                        new ParameterizedTypeReference<>() {
                        });

        if (response.getStatusCode().equals(HttpStatus.OK)) {
            return response.getBody();
        }
        return null;
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

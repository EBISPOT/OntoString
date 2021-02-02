package uk.ac.ebi.spot.ontotools.curation.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Trait;
import uk.ac.ebi.spot.ontotools.curation.rest.assembler.TraitDtoAssembler;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.TraitDto;
import uk.ac.ebi.spot.ontotools.curation.service.MappingService;
import uk.ac.ebi.spot.ontotools.curation.service.OntologyTermService;
import uk.ac.ebi.spot.ontotools.curation.service.TraitsService;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = GeneralCommon.API_V1 + CurationConstants.API_TRAITS)
public class TraitsController {

    private static final Logger log = LoggerFactory.getLogger(TraitsController.class);

    @Autowired
    private TraitsService traitsService;

    @Autowired
    private MappingService mappingService;

    @Autowired
    private OntologyTermService ontologyTermService;

    /**
     * GET /v1/traits
     */
    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseStatus(HttpStatus.OK)
    public List<TraitDto> getTraits() {
        log.info("Request to get traits.");
        List<Trait> traits = traitsService.getTraits();
        List<String> traitIds = traits.stream().map(Trait::getId).collect(Collectors.toList());
        Map<String, Mapping> mappingMap = mappingService.getMappingsByTrait(traitIds);

        List<String> ontoTermIds = new ArrayList<>();
        for (Mapping mapping : mappingMap.values()) {
            if (!ontoTermIds.contains(mapping.getMappedTermId())) {
                ontoTermIds.add(mapping.getMappedTermId());
            }
        }
        Map<String, OntologyTerm> ontologyTerms = ontologyTermService.getOntologyTermsById(ontoTermIds);

        List<TraitDto> result = new ArrayList<>();
        for (Trait trait : traits) {
            result.add(TraitDtoAssembler.assemble(trait, mappingMap.get(trait.getId()), ontologyTerms.get(mappingMap.get(trait.getId()).getMappedTermId())));

        }
        return result;
    }

}

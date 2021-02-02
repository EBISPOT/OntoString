package uk.ac.ebi.spot.ontotools.curation;

import com.fasterxml.jackson.core.type.TypeReference;
import org.apache.commons.lang3.RandomStringUtils;
import org.joda.time.DateTime;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import uk.ac.ebi.spot.ontotools.curation.constants.CurationConstants;
import uk.ac.ebi.spot.ontotools.curation.domain.Mapping;
import uk.ac.ebi.spot.ontotools.curation.domain.OntologyTerm;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.domain.Trait;
import uk.ac.ebi.spot.ontotools.curation.repository.MappingRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.OntologyTermRepository;
import uk.ac.ebi.spot.ontotools.curation.repository.TraitRepository;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.TraitDto;
import uk.ac.ebi.spot.ontotools.curation.system.GeneralCommon;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class TraitsControllerTest extends IntegrationTest {

    @Autowired
    private TraitRepository traitRepository;

    @Autowired
    private OntologyTermRepository ontologyTermRepository;

    @Autowired
    private MappingRepository mappingRepository;

    private OntologyTerm ontologyTerm;

    private Trait trait;

    @Override
    public void setup() {
        super.setup();
        Provenance provenance = new Provenance(super.user.getId(), DateTime.now());

        ontologyTerm = ontologyTermRepository.insert(new OntologyTerm(RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10),
                RandomStringUtils.randomAlphabetic(10)));
        trait = traitRepository.insert(new Trait("test", provenance));
        mappingRepository.insert(new Mapping(trait.getId(), ontologyTerm.getId(), provenance));
    }

    /**
     * GET /v1/traits
     */
    @Test
    public void shouldGetTraits() throws Exception {
        String endpoint = GeneralCommon.API_V1 + CurationConstants.API_TRAITS;

        String response = mockMvc.perform(get(endpoint)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        List<TraitDto> actual = mapper.readValue(response, new TypeReference<List<TraitDto>>() {
        });
        assertEquals(1, actual.size());
        TraitDto traitDto = actual.get(0);

        assertEquals(trait.getName(), traitDto.getName());
        assertEquals(ontologyTerm.getCurie(), traitDto.getCurrentMapping().getOntologyTerm().getCurie());
    }
}

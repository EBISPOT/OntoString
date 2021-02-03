package uk.ac.ebi.spot.ontotools.curation;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.oxo.OXOMappingResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.OXOService;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class OXOServiceTest extends IntegrationTest {

    @Autowired
    private OXOService oxoService;

    @Test
    public void shouldRetrieveTerms() {
        String termId = "http://www.orpha.net/ORDO/Orphanet_15";
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "Orphanet"});


        List<OXOMappingResponseDto> terms = oxoService.findMapping(Arrays.asList(new String[]{termId}), ontologies);
        assertEquals(3, terms.size());

        List<String> curies = terms.stream().map(OXOMappingResponseDto::getCurie).collect(Collectors.toList());
        assertTrue(curies.contains("MONDO:0007793"));
        assertTrue(curies.contains("MONDO:0007037"));
        assertTrue(curies.contains("MONDO:0014658"));
    }

}

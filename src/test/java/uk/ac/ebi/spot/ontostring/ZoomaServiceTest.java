package uk.ac.ebi.spot.ontostring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontostring.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontostring.service.ZoomaService;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

public class ZoomaServiceTest extends IntegrationTest {

    @Autowired
    private ZoomaService zoomaService;

    @Test
    public void shouldAnnotateDatasources() {
        String entity = "achondroplasia";

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});

        List<ZoomaResponseDto> annotationResults = zoomaService.annotate(entity, datasources, null);
        assertTrue(annotationResults.size() > 0);
        for (ZoomaResponseDto responseDto : annotationResults) {
            String semanticTag = responseDto.getSemanticTags().get(0);
            if (semanticTag.contains("orpha")) {
                assertEquals("http://www.orpha.net/ORDO/Orphanet_15", semanticTag);
                assertEquals("GOOD", responseDto.getConfidence());
            }
        }
    }

    @Test
    public void shouldAnnotateOntologies() {
        String entity = "achondroplasia";
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo"});

        List<ZoomaResponseDto> annotationResults = zoomaService.annotate(entity, null, ontologies);
        assertTrue(annotationResults.size() > 0);
        for (ZoomaResponseDto responseDto : annotationResults) {
            String semanticTag = responseDto.getSemanticTags().get(0);
            if (semanticTag.contains("orpha")) {
                assertEquals("http://www.orpha.net/ORDO/Orphanet_15", semanticTag);
                assertEquals("GOOD", responseDto.getConfidence());
            }
        }
    }
}

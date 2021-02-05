package uk.ac.ebi.spot.ontotools.curation;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.zooma.ZoomaResponseDto;
import uk.ac.ebi.spot.ontotools.curation.service.ZoomaService;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

public class ZoomaServiceTest extends IntegrationTest {

    @Autowired
    private ZoomaService zoomaService;

    @Test
    public void shouldAnnotateDatasources() {
        String entity = "achondroplasia";
        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});

        List<ZoomaResponseDto> annotationResults = zoomaService.annotate(entity, datasources, null);
        assertEquals(1, annotationResults.size());
        ZoomaResponseDto responseDto = annotationResults.get(0);
        assertEquals("GOOD", responseDto.getConfidence());
        assertEquals(1, responseDto.getSemanticTags().size());
        assertEquals("http://www.orpha.net/ORDO/Orphanet_15", responseDto.getSemanticTags().get(0));

    }

    @Test
    public void shouldAnnotateOntologies() {
        String entity = "achondroplasia";
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo"});

        List<ZoomaResponseDto> annotationResults = zoomaService.annotate(entity, null, ontologies);
        assertEquals(2, annotationResults.size());
        for (ZoomaResponseDto responseDto : annotationResults) {
            assertEquals("MEDIUM", responseDto.getConfidence());
            assertEquals(1, responseDto.getSemanticTags().size());
            String semanticTag = responseDto.getSemanticTags().get(0);
            if (semanticTag.contains("orpha")) {
                assertEquals("http://www.orpha.net/ORDO/Orphanet_15", semanticTag);
            } else {
                assertEquals("http://purl.obolibrary.org/obo/MONDO_0007037", semanticTag);
            }
        }
    }
}

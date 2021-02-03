package uk.ac.ebi.spot.ontotools.curation;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontotools.curation.service.ZoomaService;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static junit.framework.TestCase.assertTrue;
import static org.junit.Assert.assertEquals;

public class ZoomaServiceTest extends IntegrationTest {

    @Autowired
    private ZoomaService zoomaService;

    @Test
    public void shouldAnnotateDatasources() {
        String entity = "achondroplasia";
        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});

        Map<String, List<String>> annotationResults = zoomaService.annotate(entity, datasources, null);
        assertEquals(1, annotationResults.size());
        String key = annotationResults.keySet().iterator().next();
        assertEquals("GOOD", key);
        assertEquals(1, annotationResults.get(key).size());
        assertEquals("http://www.orpha.net/ORDO/Orphanet_15", annotationResults.get(key).get(0));

    }

    @Test
    public void shouldAnnotateOntologies() {
        String entity = "achondroplasia";
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo"});

        Map<String, List<String>> annotationResults = zoomaService.annotate(entity, null, ontologies);
        assertEquals(1, annotationResults.size());
        String key = annotationResults.keySet().iterator().next();
        assertEquals("MEDIUM", key);
        assertEquals(2, annotationResults.get(key).size());
        assertTrue(annotationResults.get(key).contains("http://www.orpha.net/ORDO/Orphanet_15"));
        assertTrue(annotationResults.get(key).contains("http://purl.obolibrary.org/obo/MONDO_0007037"));
    }
}

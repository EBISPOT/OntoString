package uk.ac.ebi.spot.ontostring;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectDto;
import uk.ac.ebi.spot.ontostring.service.OLSService;
import uk.ac.ebi.spot.ontostring.service.ProjectService;
import uk.ac.ebi.spot.ontostring.constants.CurationConstants;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class OLSServiceTest extends IntegrationTest {

    @Autowired
    private OLSService olsService;

    @Autowired
    private ProjectService projectService;

    private Project project;

    @Override
    public void setup() throws Exception {
        super.setup();

        List<String> datasources = Arrays.asList(new String[]{"cttv", "sysmicro", "atlas", "ebisc", "uniprot", "gwas", "cbi", "clinvar-xrefs"});
        List<String> ontologies = Arrays.asList(new String[]{"efo", "mondo", "hp", "ordo", "orphanet"});
        ProjectDto projectDto = super.createProject("New Project", user1, datasources, ontologies, "efo", 0, null);
        user1 = userService.findByEmail(user1.getEmail());
        project = projectService.retrieveProject(projectDto.getId(), user1);
    }

    @Test
    public void shouldRetrieveTerms() {
        String ontoId = "ordo";
        String termId = "http://www.orpha.net/ORDO/Orphanet_15";

        List<OLSTermDto> terms = olsService.retrieveTerms(ontoId, termId);
        assertEquals(1, terms.size());
        assertEquals(termId, terms.get(0).getIri());
        assertEquals("Achondroplasia", terms.get(0).getLabel());
        assertEquals("Orphanet:15", terms.get(0).getCurie());
        assertFalse(terms.get(0).getObsolete());
    }

    @Test
    public void shouldQuery() {
        String prefix = "diabetes";
        List<OLSQueryDocDto> docs = olsService.query(prefix, project, CurationConstants.CONTEXT_DEFAULT, false, false);
        assertEquals(10, docs.size());

        List<String> efoUris = new ArrayList<>();
        for (OLSQueryDocDto olsQueryDocDto : docs) {
            if (olsQueryDocDto.getOntologyName().equalsIgnoreCase("efo")) {
                efoUris.add(olsQueryDocDto.getCurie());
            }
        }
        assertTrue(efoUris.contains("EFO:0000400"));
    }

    @Test
    public void shouldGetTerm() {
        String iri = "http://www.ebi.ac.uk/efo/EFO_0001444";
        OLSTermDto olsTermDto = olsService.retrieveOriginalTerm(iri, true);
        assertNotNull(olsTermDto);

        iri = "EFO:0001444";
        olsTermDto = olsService.retrieveOriginalTerm(iri, false);
        assertNotNull(olsTermDto);
    }

    @Test
    public void shouldRetrieveAncestors() {
        String ontoId = "efo";
        String termId = "http://www.orpha.net/ORDO/Orphanet_15";

        List<OLSTermDto> terms = olsService.retrieveAncestors(ontoId, termId, false);
        assertEquals(23, terms.size());
    }
}

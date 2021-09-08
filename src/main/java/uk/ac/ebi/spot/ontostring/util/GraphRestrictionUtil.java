package uk.ac.ebi.spot.ontostring.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import uk.ac.ebi.spot.ontostring.service.OLSService;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;
import uk.ac.ebi.spot.ontostring.rest.dto.project.ProjectContextGraphRestrictionDto;

import java.util.ArrayList;
import java.util.List;

@Component
public class GraphRestrictionUtil {

    @Autowired
    private OLSService olsService;

    public ProjectContextGraphRestrictionDto enrichGraphRestriction(ProjectContextGraphRestrictionDto pcgr) {
        List<String> iris = new ArrayList<>();
        for (String cls : pcgr.getClasses()) {
            if (cls.startsWith("http://")) {
                iris.add(cls);
            } else {
                OLSTermDto olsTermDto = olsService.retrieveOriginalTerm(cls, false);
                if (olsTermDto != null) {
                    iris.add(olsTermDto.getIri());
                } else {
                    iris.add(cls);
                }
            }
        }

        return new ProjectContextGraphRestrictionDto(pcgr.getClasses(),
                iris,
                pcgr.getRelations(),
                pcgr.getDirect(),
                pcgr.getIncludeSelf());
    }

}

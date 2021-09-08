package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.Project;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontostring.rest.dto.ols.OLSTermDto;

import java.util.List;

public interface OLSService {

    List<OLSTermDto> retrieveAncestors(String ontologyId, String iri, boolean direct);

    List<OLSTermDto> retrieveTerms(String ontologyId, String identifierValue);

    List<OLSQueryDocDto> query(String prefix, Project project, String context, boolean usePreferred, boolean useGraphRestrictions);

    OLSTermDto retrieveOriginalTerm(String iri, boolean retrieveByIRI);
}

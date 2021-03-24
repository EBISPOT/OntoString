package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSQueryDocDto;
import uk.ac.ebi.spot.ontotools.curation.rest.dto.ols.OLSTermDto;

import java.util.List;

public interface OLSService {

    List<OLSTermDto> retrieveTerms(String ontologyId, String identifierValue);

    List<OLSQueryDocDto> query(String prefix);

    OLSTermDto retrieveOriginalTerm(String iri);
}

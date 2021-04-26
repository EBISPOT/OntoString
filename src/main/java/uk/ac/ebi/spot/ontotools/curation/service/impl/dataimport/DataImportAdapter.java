package uk.ac.ebi.spot.ontotools.curation.service.impl.dataimport;

import uk.ac.ebi.spot.ontotools.curation.domain.Source;
import uk.ac.ebi.spot.ontotools.curation.domain.auth.User;

import java.io.InputStream;

public interface DataImportAdapter {

    String importData(InputStream fileInputStream, String projectId, Source source, User user);
}

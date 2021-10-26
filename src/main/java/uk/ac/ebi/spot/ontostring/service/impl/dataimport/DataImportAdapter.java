package uk.ac.ebi.spot.ontostring.service.impl.dataimport;

import uk.ac.ebi.spot.ontostring.domain.Source;
import uk.ac.ebi.spot.ontostring.domain.auth.User;

import java.io.InputStream;

public interface DataImportAdapter {

    String importData(InputStream fileInputStream, String projectId, Source source, User user);
}

package uk.ac.ebi.spot.ontostring.service.impl.dataimport;

public interface DataImportFactory {

    DataImportAdapter getAdapter(String fileType);
}

package uk.ac.ebi.spot.ontotools.curation.service.impl.dataimport;

public interface DataImportFactory {

    DataImportAdapter getAdapter(String fileType);
}

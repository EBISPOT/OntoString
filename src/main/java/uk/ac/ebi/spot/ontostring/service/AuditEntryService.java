package uk.ac.ebi.spot.ontostring.service;

import uk.ac.ebi.spot.ontostring.domain.AuditEntry;
import uk.ac.ebi.spot.ontostring.domain.MetadataEntry;
import uk.ac.ebi.spot.ontostring.domain.Provenance;

import java.util.List;

public interface AuditEntryService {

    void addEntry(String action, String entityId, Provenance provenance, List<MetadataEntry> metadataEntryList);

    List<AuditEntry> retrieveAuditEntries(String entityId);
}

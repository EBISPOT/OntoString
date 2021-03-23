package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.AuditEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.MetadataEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

import java.util.List;

public interface AuditEntryService {

    void addEntry(String action, String entityId, Provenance provenance, List<MetadataEntry> metadataEntryList);

    List<AuditEntry> retrieveAuditEntries(String entityId);
}

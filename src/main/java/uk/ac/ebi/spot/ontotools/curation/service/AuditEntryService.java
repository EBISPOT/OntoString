package uk.ac.ebi.spot.ontotools.curation.service;

import uk.ac.ebi.spot.ontotools.curation.domain.AuditEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;

import java.util.List;
import java.util.Map;

public interface AuditEntryService {

    void addEntry(String action, String entityId, Provenance provenance, Map<String, String> metadata);

    List<AuditEntry> retrieveAuditEntries(String entityId);
}

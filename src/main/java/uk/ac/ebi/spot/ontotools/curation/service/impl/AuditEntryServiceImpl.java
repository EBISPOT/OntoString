package uk.ac.ebi.spot.ontotools.curation.service.impl;

import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import uk.ac.ebi.spot.ontotools.curation.domain.AuditEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.MetadataEntry;
import uk.ac.ebi.spot.ontotools.curation.domain.Provenance;
import uk.ac.ebi.spot.ontotools.curation.repository.AuditEntryRepository;
import uk.ac.ebi.spot.ontotools.curation.service.AuditEntryService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
public class AuditEntryServiceImpl implements AuditEntryService {

    @Autowired
    private AuditEntryRepository auditEntryRepository;

    @Override
    @Async
    public void addEntry(String action, String entityId, Provenance provenance, Map<String, String> metadata) {
        List<MetadataEntry> metadataEntryList = new ArrayList<>();
        for (String key : metadata.keySet()) {
            metadataEntryList.add(new MetadataEntry(key, metadata.get(key)));
        }

        auditEntryRepository.insert(new AuditEntry(null, entityId, action, provenance, metadataEntryList, DateTime.now()));
    }

    @Override
    public List<AuditEntry> retrieveAuditEntries(String entityId) {
        return auditEntryRepository.findByEntityIdOrderByTimestampDesc(entityId);
    }
}

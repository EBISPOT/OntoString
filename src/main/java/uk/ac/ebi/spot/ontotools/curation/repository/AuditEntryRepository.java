package uk.ac.ebi.spot.ontotools.curation.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontotools.curation.domain.AuditEntry;

import java.util.List;

public interface AuditEntryRepository extends MongoRepository<AuditEntry, String> {

    List<AuditEntry> findByEntityIdOrderByTimestampDesc(String entityId);
}

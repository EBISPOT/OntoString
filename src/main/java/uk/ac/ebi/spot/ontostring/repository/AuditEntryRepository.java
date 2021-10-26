package uk.ac.ebi.spot.ontostring.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import uk.ac.ebi.spot.ontostring.domain.AuditEntry;

import java.util.List;

public interface AuditEntryRepository extends MongoRepository<AuditEntry, String> {

    List<AuditEntry> findByEntityIdOrderByTimestampDesc(String entityId);
}

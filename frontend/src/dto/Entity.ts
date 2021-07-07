import MappingSuggestion from "./MappingSuggestion";
import Provenance from "./Provenance";
import Source from "./Source";
import Mapping from "./Mapping";
import AuditEntry from "./AuditEntry";

export default interface Entity {
    id: string
    name: string
    mappingStatus: EntityStatus
    source: Source
    upstreamId: string
    upstreamField: string
    mappingSuggestions:MappingSuggestion[]
    mapping:Mapping
    created:Provenance
    auditTrail:AuditEntry[]
    context: string
}

export enum EntityStatus {
    UNMAPPED = 'UNMAPPED',
    SUGGESTIONS_PROVIDED = 'SUGGESTIONS_PROVIDED',
    AUTO_MAPPED = 'AUTO_MAPPED',
    MANUALLY_MAPPED = 'MANUALLY_MAPPED'
}

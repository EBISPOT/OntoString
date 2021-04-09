import OntologyTerm from "./OntologyTerm";
import Provenance from "./Provenance";

export default interface MappingSuggestion {
    id:string
    entityId:string
    ontologyTerm: OntologyTerm
    created: Provenance
}

import OntologyTerm from "./OntologyTerm";

export default interface TermListing {
    ontologyTerm:OntologyTerm
    entities:{
        id:string
        name:string
    }[]
}

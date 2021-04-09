
/*

On creation:

curie, iri and label are mandatory NOT EMPTY
In responses:

status is auto-populated
*/

export default interface OntologyTerm {
    curie:string
    iri:string
    label:string
    status:OntologyTermStatus
    description:string
    crossRefs:string
}

export enum OntologyTermStatus {
    DELETED = 'DELETED',
    OBSOLETE = 'OBSOLETE',
    CURRENT = 'CURRENT',
    NEEDS_IMPORT = 'NEEDS_IMPORT'
}


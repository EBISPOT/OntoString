
import Review from './Review'
import Comment from './Comment'
import Provenance from './Provenance'
import OntologyTerm from './OntologyTerm'

export interface CreateMapping {
    entityId:string
    // ontologyTerms: {
    //     curie:string
    //     iri:string
    //     label:string
    //     status:string
    // }[]
    ontologyTerms:OntologyTerm[]
}

export default interface Mapping {
    id?:string
    entityId:string
    ontologyTerms: OntologyTerm[]
    reviewed:boolean
    status:MappingStatus
    reviews:Review[]
    comments:Comment[]
    created: Provenance
}

export enum MappingStatus {
    AWAITING_REVIEW = 'AWAITING_REVIEW',
    REVIEW_IN_PROGRESS = 'REVIEW_IN_PROGRESS',
    REQUIRED_REVIEWS_REACHED = 'REQUIRED_REVIEWS_REACHED'
}


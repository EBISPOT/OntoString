
import Provenance from "./Provenance";

export default interface Context {
    id?:string
    name:string
    description:string
    datasources:{
        field:string
        mappingList:string[]
    }[]
    ontologies:{
        field:string
        mappingList:string[]
    }[]
    preferredMappingOntologies:string[]
    created:Provenance,
    graphRestriction?: {
        classes:string[],
        relations:string[],
        direct:boolean,
        include_self:boolean
    }
}



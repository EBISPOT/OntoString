
export default interface OlsResponse {
    numFound:number
    start:number
    docs: OlsSearchResult[]
}

export interface OlsSearchResult {

        iri:string
        curie:string
        label:string
        description:string
        ontologyName:string

}

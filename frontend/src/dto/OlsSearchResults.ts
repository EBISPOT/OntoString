
export interface OlsSearchResults {

    results: {
        ALL:OlsSearchResult[]
        GRAPH_RESTRICTION:OlsSearchResult[]
        PREFERRED_ONTOLOGIES:OlsSearchResult[]
    }

}

export interface OlsSearchResult {

        iri:string
        obo_id:string
        label:string
        description:string[]
        ontology_name:string

}

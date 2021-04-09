
export default interface Paginated<T> {
    content:T[]
    number:number
    size:number
    totalElements:number
    pageable: {
        sort: {
            sorted:boolean
            unsorted:boolean
            empty:boolean
        }
        offset:number
        pageNumber:number
        pageSize:number
        paged:boolean
        unpaged:boolean
    }
    totalPages:number
    sort: {
            sorted:boolean
            unsorted:boolean
            empty:boolean
    }
    first:boolean
    last:boolean
    numberOfElements:number
    empty:boolean
}


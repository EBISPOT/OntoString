
export default interface User {
    id?: string
    name: string
    email: string
    superUser?:boolean
    roles: {
        projectId: string
        role:'CONTRIBUTOR'|'CONSUMER'|'ADMIN'
    }[]
}


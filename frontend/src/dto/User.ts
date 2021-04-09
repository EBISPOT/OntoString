
export default interface User {
    id: string
    name: string
    email: string
    roles: {
        projectId: string
        role:'CONTRIBUTOR'|'CONSUMER'|'ADMIN'
    }[]
}


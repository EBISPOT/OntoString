
export default interface AuditEntry {
    action:string
    user: {
        name:string
        email:string
    }
    metadata: {
        key:string
        value:string
    }[]
    timestamp:string
}


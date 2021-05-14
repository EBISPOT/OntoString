
/*
On creation:

id is NOT ACCEPTED
name is mandatory NOT EMPTY
In responses:

id is NOT EMPTY
created and lastUpdated follow a standard structure across all DTOs / objects
timestamp - always using the format: YYYY-MM-DDThh:mm:ss.ms+TZ
user - always containing name and email
*/

import Provenance from "./Provenance";

export default interface Source {
    id?:string
    name:string
    description:string
    uri:string
    type:'LOCAL'|'REMOTE'
    created?: Provenance
    lastUpdated?: Provenance
}

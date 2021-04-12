
/*
On creation:

id is NOT ACCEPTED
name is mandatory NOT EMPTY
datasources and ontologies fields are list of ProjectMappingConfigDto. They enable field-level preferences for mappings. If general non-field-specific mapping is preferred / used, the value of field becomes a constant: ALL
In responses:

id is NOT EMPTY
created follows a standard structure across all DTOs / objects
timestamp - always using the format: YYYY-MM-DDThh:mm:ss.ms+TZ
user - always containing name and email
*/

import Context from "./Context";

export default interface Project extends Context {
    numberOfReviewsRequired:number
    contexts:Context[]
}


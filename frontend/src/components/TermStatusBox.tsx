
import React from "react";
import { OntologyTermStatus } from "../dto/OntologyTerm";
import StatusBox from "./StatusBox";

export default function TermStatusBox(props:{ status:OntologyTermStatus }) {
    
    let { status } = props

    let color = ({
        [OntologyTermStatus.DELETED]: '#B22929',
        [OntologyTermStatus.OBSOLETE]: '#B22929',
        [OntologyTermStatus.NEEDS_IMPORT]: '#FAA869',
        [OntologyTermStatus.CURRENT]: '#29B27F'
    })[status]

    let text = ({
        [OntologyTermStatus.DELETED]: 'Deleted',
        [OntologyTermStatus.OBSOLETE]: 'Obsolete',
        [OntologyTermStatus.NEEDS_IMPORT]: 'Needs Import',
        [OntologyTermStatus.CURRENT]: 'Current'
    })[status]

    return <StatusBox color={color} text={text} />
}



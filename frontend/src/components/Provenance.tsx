
import ProvenanceDTO from "../dto/Provenance";
import formatDate from '../formatDate'

export default function Provenance(props:{provenance:ProvenanceDTO, label:string}) {

    let { provenance, label } = props

    let when = formatDate(provenance.timestamp)

    return <p>
        <i>
        {label} <a style={{color: 'black',textDecoration:'none'}} href={"mailto:" + provenance.user.email}>{provenance.user.name}</a> {when}
        </i>
    </p>
}




import React from "react";
import { MappingStatus } from "../dto/Mapping";
import StatusBox from "./StatusBox";

export default function MappingStatusBox(props:{ status:MappingStatus }) {
    
    let { status } = props

    let color = ({
        [MappingStatus.AWAITING_REVIEW]: '#B22929',
        [MappingStatus.REVIEW_IN_PROGRESS]: '#FAA869',
        [MappingStatus.REQUIRED_REVIEWS_REACHED]: '#29B27F'
    })[status]

    let text = ({
        [MappingStatus.AWAITING_REVIEW]: 'Awaiting Review',
        [MappingStatus.REVIEW_IN_PROGRESS]: 'Review in Progress',
        [MappingStatus.REQUIRED_REVIEWS_REACHED]: 'Required Reviews Reached'
    })[status]

    return <StatusBox color={color} text={text} />
}



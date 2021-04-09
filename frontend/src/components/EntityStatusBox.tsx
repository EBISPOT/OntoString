
import React from "react";
import { EntityStatus } from "../dto/Entity";
import StatusBox from "./StatusBox";

export default function EntityStatusBox(props:{ status:EntityStatus }) {
    
    let { status } = props

    let color = ({
        [EntityStatus.UNMAPPED]: '#B22929',
        [EntityStatus.SUGGESTIONS_PROVIDED]: '#FAA869',
        [EntityStatus.MANUALLY_MAPPED]: '#29B27F',
        [EntityStatus.AUTO_MAPPED]: '#29B27F'
    })[status]

    let text = ({
        [EntityStatus.UNMAPPED]: 'Unmapped',
        [EntityStatus.SUGGESTIONS_PROVIDED]: 'Suggestions Provided',
        [EntityStatus.MANUALLY_MAPPED]: 'Manually Mapped',
        [EntityStatus.AUTO_MAPPED]: 'Auto Mapped'
    })[status]

    return <StatusBox color={color} text={text} />
}



import React, { Fragment } from "react";
import Header from "../components/Header";
import HelpSection from "../components/HelpSection";

export default function Help(props:{projectId?:string}) {
    return <Fragment>
        <Header section='help' projectId={props.projectId} />
        <main>

<HelpSection title="Projects and Contexts"></HelpSection>
<HelpSection title="Uploading Entities"></HelpSection>
<HelpSection title="Actioning Terms"></HelpSection>
<HelpSection title="Using the API"></HelpSection>
</main>

    </Fragment>
}


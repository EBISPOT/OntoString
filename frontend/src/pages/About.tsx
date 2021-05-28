import React, { Fragment } from "react";
import Header from "../components/Header";

export default function About(props:{projectId?:string}) {
    return <Fragment>
        <Header section='about' projectId={props.projectId} />
        <main>
OntoString is a tool for curating mappings from free text to ontology terms.
        </main>
    </Fragment>
}



import React from "react";
import { useState, useEffect } from "react";
import Project from "../dto/Project";

export interface Props {
}

export interface State {
    projects:Project[]
}

export default class ProjectList extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            projects: []
        }

        this.fetchProjects()

    }

    render() {
        return <div/>
    }

    async fetchProjects() {


    }
}


import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Project from "../../dto/Project";
import ProjectForm from "./ProjectForm";
import ProjectList from "./ProjectList";

interface Props {
    onCreate:(project:Project)=>void
}

interface State {
    open:boolean
    project:Project
}

class CreateProjectDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            open: false,
            project: emptyProject()
        }

    }

    render() {

        let { open, project } = this.state

        console.dir(project)

        return <div>
            <Button variant="outlined" color="primary" onClick={this.onOpen}>
                + Create Project
            </Button>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Create Project</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Please enter a name and description for the project.
                    </DialogContentText>
                    <Box m={2}>
                        <ProjectForm project={project} onUpdateProject={this.onUpdateProject} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onClose} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onCreate} color="primary" variant="outlined" disabled={!project.name || !project.description}>
                        Create
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    }

    onOpen = () => {
        this.setState(prevState => ({ ...prevState, open: true, project: emptyProject() }))
    }

    onClose = () => {
        this.setState(prevState => ({ ...prevState, open: false }))
    }

    onUpdateProject = (project:Project) => {
        this.setState(prevState => ({ ...prevState, project }))
    }

    onCreate = () => {
        this.props.onCreate(this.state.project)
        this.setState(prevState => ({ ...prevState, open: false }))
    }
}

export default CreateProjectDialog


function emptyProject() {
    return {
        name: '',
        description: '',
        datasources: [],
        ontologies: [],
        preferredMappingOntologies: [],
        contexts: [],
        numberOfReviewsRequired: 0,
        created: {
            user: {
                email: '',
                name: ''
            },
            timestamp: ''
        }

    }
}



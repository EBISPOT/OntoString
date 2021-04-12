
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import { OlsSearchResult } from "../../dto/OlsSearchResult";
import Project from "../../dto/Project";
import OntologyTermSearchBox from "./OntologyTermSearchBox";

interface Props {
    open:boolean
    onClose:()=>void
    project:Project
    onSelectTerm:(term:OlsSearchResult) => void
}

interface State {
}

class SearchOntologiesDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
        }

    }

    render() {

        let { open, project } = this.props

        console.dir(project)

        return <div>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Search Ontologies</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                    </DialogContentText>
                    <Box m={2}>
                        <OntologyTermSearchBox projectId={project.id!} onSelectTerm={this.onSelectTerm} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onClose} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    {/* <Button onClick={this.onCreate} color="primary" variant="outlined" disabled={!project.name || !project.description}>
                        Create
                    </Button> */}
                </DialogActions>
            </Dialog>
        </div>
    }

    onClose = () => {
        this.props.onClose()
    }

    onSelectTerm = (term:OlsSearchResult) => {
        this.props.onSelectTerm(term)
    }

}

export default SearchOntologiesDialog





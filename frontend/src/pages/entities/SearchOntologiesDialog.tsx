
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Tab, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tabs, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import { OlsSearchResult } from "../../dto/OlsSearchResults";
import Project from "../../dto/Project";
import OntologyTermSearchBox from "./OntologyTermSearchBox";

interface Props {
    open:boolean
    onClose:()=>void
    project:Project
    contextName:string
    onSelectTerm:(term:OlsSearchResult) => void
}

interface State {
    tab:string
}


class SearchOntologiesDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            tab: 'GRAPH_RESTRICTION'
        }

    }

    render() {

        let { open, project, contextName } = this.props

        console.dir(project)

        return <div>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Search Ontologies</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                    </DialogContentText>
                    <Tabs value={this.state.tab} onChange={this.changeTab}>
                        <Tab label="Restricted" value={'GRAPH_RESTRICTION'} />
                        <Tab label="Preferred Ontologies" value='PREFERRED_ONTOLOGIES' />
                        <Tab label="All Terms" value='ALL'  />
                    </Tabs>
                    <Box m={2}>
                        <OntologyTermSearchBox projectId={project.id!} contextName={contextName} onSelectTerm={this.onSelectTerm}
                                    mode={this.state.tab} />
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

    changeTab = (e:any, tab:string) => {
        this.setState({tab})
    }

}

export default SearchOntologiesDialog





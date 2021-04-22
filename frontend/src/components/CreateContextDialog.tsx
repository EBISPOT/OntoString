
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../auth";
import Context from "../dto/Context";
import ContextForm from "./ContextForm";

interface Props {
    onCreate:(context:Context)=>void
    onClose:()=>void
}

interface State {
    context:Context
}

class CreateContextDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            context: emptyContext()
        }

    }

    render() {

        let { context } = this.state

        console.dir(context)

        return <Dialog open={true} onClose={this.onClose}>
                <DialogTitle>Create Context</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Please enter a name and description for the context.
                    </DialogContentText>
                    <Box m={2}>
                        <ContextForm context={context} onUpdateContext={this.onUpdateContext} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onClose} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onCreate} color="primary" variant="outlined" disabled={!context.name || !context.description}>
                        Create
                    </Button>
                </DialogActions>
            </Dialog>
    }

    onClose = () => {
        this.props.onClose()
    }

    onUpdateContext = (context:Context) => {
        this.setState(prevState => ({ ...prevState, context }))
    }

    onCreate = () => {
        this.props.onCreate(this.state.context)
        this.setState(prevState => ({ ...prevState, open: false }))
    }
}

export default CreateContextDialog


function emptyContext() {
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



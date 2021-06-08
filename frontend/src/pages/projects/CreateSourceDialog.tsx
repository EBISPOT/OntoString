
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Source from "../../dto/Source";
import SourceForm from "./SourceForm";
import SourceList from "./SourceList";

interface Props {
    onCreate:(source:Source)=>void
}

interface State {
    open:boolean
    source:Source
}

class CreateSourceDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            open: false,
            source: emptySource()
        }

    }

    render() {

        let { open, source } = this.state

        return <div>
            <Button variant="outlined" color="primary" onClick={this.onOpen}>
<<<<<<< HEAD
                + Create Source
            </Button>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Create Source</DialogTitle>
=======
                + Add Datasource
            </Button>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Add Datasource</DialogTitle>
>>>>>>> 56b430299b6decb668f997be7d79f6351affebe7
                <DialogContent>
                    <Box m={2}>
                        <SourceForm source={source} onUpdateSource={this.onUpdateSource} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onClose} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onCreate} color="primary" variant="outlined" disabled={!source.name || !source.description}>
<<<<<<< HEAD
                        Create
=======
                        Add
>>>>>>> 56b430299b6decb668f997be7d79f6351affebe7
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    }

    onOpen = () => {
        this.setState(prevState => ({ ...prevState, open: true, source: emptySource() }))
    }

    onClose = () => {
        this.setState(prevState => ({ ...prevState, open: false }))
    }

    onUpdateSource = (source:Source) => {
        this.setState(prevState => ({ ...prevState, source }))
    }

    onCreate = () => {
        this.props.onCreate(this.state.source)
        this.setState(prevState => ({ ...prevState, open: false }))
    }
}

export default CreateSourceDialog


function emptySource():Source {
    return {
        name: '',
        description: '',
        uri: '',
        type: 'REMOTE',
        created: {
            user: {
                email: '',
                name: ''
            },
            timestamp: ''
        }

    }
}



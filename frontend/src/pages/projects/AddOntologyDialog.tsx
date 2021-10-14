
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";

interface Props {
    onSubmit:(ontologyId:string)=>void
    onCancel:()=>void
    open:boolean
}

interface State {
    ontologyId:string
}

class AddOntologyDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            ontologyId: ''
        }

    }

    render() {

        let { open } = this.props
        let { ontologyId } = this.state

        return <Dialog open={open} onClose={this.onCancel}>
                <DialogTitle>Add Ontology</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Please enter the ontology ID as it appears in the Ontology Lookup Service on the right hand side of the ontology page. For example, the OLS identifier for EFO is <code>efo</code>.
                    </DialogContentText>
                <br/>
                <TextField size="small" value={ontologyId} variant="outlined" style={{ minWidth: '400px' }} onChange={this.onChangeOntologyId} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onCancel} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onSubmit} color="primary" variant="outlined" disabled={!ontologyId}>
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
    }

    onCancel = () => {
        this.props.onCancel();
    }

    onSubmit = () => {
        this.props.onSubmit(this.state.ontologyId)
    }

    onChangeOntologyId = (e:any) => {
        this.setState(prevState => ({ ...prevState, ontologyId: e.target.value as string }))
    }

}

export default AddOntologyDialog

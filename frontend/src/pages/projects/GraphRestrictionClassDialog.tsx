
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";

interface Props {
    onSubmit:(term:string)=>void
    onCancel:()=>void
    open:boolean
}

interface State {
    term:string
}

class GraphRestrictionClassDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            term: ''
        }

    }

    render() {

        let { open } = this.props
        let { term } = this.state

        return <Dialog open={open} onClose={this.onCancel}>
                <DialogTitle>Add Graph Restriction Class</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Please enter the term for a class in Compact URI (CURIE) form as it appears in the Ontology Lookup Service. For example, the MONDO term &quot;disease or disorder&quot; would be written as <code>MONDO:0000001</code>.
                    </DialogContentText>
                <br/>
                <TextField size="small" value={term} variant="outlined" style={{ minWidth: '400px' }} onChange={this.onChangeTerm} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onCancel} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onSubmit} color="primary" variant="outlined" disabled={!term}>
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
    }

    onCancel = () => {
        this.props.onCancel();
    }

    onSubmit = () => {
        this.props.onSubmit(this.state.term)
    }

    onChangeTerm = (e:any) => {
        this.setState(prevState => ({ ...prevState, term: e.target.value as string }))
    }

}

export default GraphRestrictionClassDialog


import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";

interface Props {
    onSubmit:(datasourceId:string)=>void
    onCancel:()=>void
    open:boolean
}

interface State {
    datasourceId:string
}

class AddDatasourceDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            datasourceId: ''
        }

    }

    render() {

        let { open } = this.props
        let { datasourceId } = this.state

        return <Dialog open={open} onClose={this.onCancel}>
                <DialogTitle>Add Datasource</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Please enter the datasource ID from ZOOMA.
                    </DialogContentText>
                <br/>
                <TextField size="small" value={datasourceId} variant="outlined" style={{ minWidth: '400px' }} onChange={this.onChangeDatasourceId} />
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onCancel} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onSubmit} color="primary" variant="outlined" disabled={!datasourceId}>
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
    }

    onCancel = () => {
        this.props.onCancel();
    }

    onSubmit = () => {
        this.props.onSubmit(this.state.datasourceId)
    }

    onChangeDatasourceId = (e:any) => {
        this.setState(prevState => ({ ...prevState, datasourceId: e.target.value as string }))
    }

}

export default AddDatasourceDialog

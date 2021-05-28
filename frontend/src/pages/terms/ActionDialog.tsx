
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";

interface Props {
    onSubmit:(comment:string)=>void
    onCancel:()=>void
    open:boolean
}

interface State {
    comment:string
}

class ActionDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            comment: ''
        }

    }

    render() {

        let { open } = this.props
        let { comment } = this.state

        return <Dialog open={open} onClose={this.onCancel}>
                <DialogTitle>Mark Terms as Actioned</DialogTitle>
                <DialogContent>
                    <DialogContentText>
                        Please enter a comment, for example "issue opened on GitHub"
                    </DialogContentText>
                    <Box m={2}>
                        <TextField multiline={true} rows={4} value={comment} variant="outlined" style={{minWidth: '400px'}}onChange={this.onChangeComment} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onCancel} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onSubmit} color="primary" variant="outlined" disabled={!comment}>
                        Submit
                    </Button>
                </DialogActions>
            </Dialog>
    }

    onCancel = () => {
        this.props.onCancel();
    }

    onSubmit = () => {
        this.props.onSubmit(this.state.comment)
    }

    onChangeComment = (e:any) => {
        this.setState(prevState => ({ ...prevState, comment: e.target.value as string }))
    }

}

export default ActionDialog

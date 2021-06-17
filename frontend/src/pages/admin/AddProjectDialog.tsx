
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Project from "../../dto/Project";
import User from "../../dto/User";
import UserForm from "./UserForm";
import UserList from "./UserList";

interface Props {
    onAdd:(project:Project)=>void
    projects:Project[]
}

interface State {
    open:boolean
    projectId:string
}

class AddProjectDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            open: false,
            projectId: ''
        }

    }

    render() {

        let { open, projectId } = this.state

        return <div>
            <Button variant="outlined" color="primary" onClick={this.onOpen}>
                + Add Project
            </Button>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Add User to Project</DialogTitle>
                <DialogContent>
                    <Box m={2}>
			    <Select fullWidth value={this.state.projectId} onChange={this.onChange}>
				    {this.props.projects.map(p => <MenuItem value={p.id}>{p.name}</MenuItem>)}
			</Select>
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onClose} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onCreate} color="primary" variant="outlined">
                        Add
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    }

    onOpen = () => {
        this.setState(prevState => ({ ...prevState, open: true, projectId: '' }))
    }

    onClose = () => {
        this.setState(prevState => ({ ...prevState, open: false }))
    }

    onChange = (ev) => {
	this.setState(prevState => ({ ...prevState, projectId: ev.target.value }))
    }

    onCreate = () => {
	if (this.state.projectId) {
		this.props.onAdd(this.props.projects.filter(p => p.id === this.state.projectId)[0])
	}
        this.setState(prevState => ({ ...prevState, open: false }))
    }
}

export default AddProjectDialog





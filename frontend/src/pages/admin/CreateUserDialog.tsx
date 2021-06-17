
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import User from "../../dto/User";
import UserForm from "./UserForm";
import UserList from "./UserList";

interface Props {
    onCreate:(user:User)=>void
}

interface State {
    open:boolean
    user:User
}

class CreateUserDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            open: false,
            user: emptyUser()
        }

    }

    render() {

        let { open, user } = this.state

        console.dir(user)

        return <div>
            <Button variant="outlined" color="primary" onClick={this.onOpen}>
                + Create User
            </Button>
            <Dialog open={open} onClose={this.onClose}>
                <DialogTitle>Create User</DialogTitle>
                <DialogContent>
                    <Box m={2}>
                        <UserForm user={user} onUpdateUser={this.onUpdateUser} />
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onClose} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onCreate} color="primary" variant="outlined" disabled={!user.name || !user.email}>
                        Create
                    </Button>
                </DialogActions>
            </Dialog>
        </div>
    }

    onOpen = () => {
        this.setState(prevState => ({ ...prevState, open: true, user: emptyUser() }))
    }

    onClose = () => {
        this.setState(prevState => ({ ...prevState, open: false }))
    }

    onUpdateUser = (user:User) => {
        this.setState(prevState => ({ ...prevState, user }))
    }

    onCreate = () => {
        this.props.onCreate(this.state.user)
        this.setState(prevState => ({ ...prevState, open: false }))
    }
}

export default CreateUserDialog


function emptyUser() {
    return {
        name: '',
        email: '',
	roles: [
	]
    }
}



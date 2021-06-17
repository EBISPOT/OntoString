
import { Button, CircularProgress, createStyles, darken, FormGroup, Grid, lighten, makeStyles, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { ChangeEvent } from "react";
import { useState, useEffect } from "react";
import User from "../../dto/User";

interface Props {
    user:User
    onUpdateUser:(user:User)=>void
}

interface State {
}

class UserForm extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

    }

    render() {

        let { user } = this.props

        return <form noValidate autoComplete='off'>
            <FormGroup>
                <Grid container direction="column">
                    <Grid item>
                        <TextField label="Name" fullWidth onChange={this.onChangeName} value={user.name}  />
                    </Grid>
                    <Grid item>
                        <TextField label="Email" fullWidth onChange={this.onChangeEmail} value={user.email}  />
                    </Grid>
                </Grid>
            </FormGroup>
        </form>
    }

    onChangeName = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateUser({ ...this.props.user, name: e.target.value })
    }

    onChangeEmail = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateUser({ ...this.props.user, email: e.target.value })
    }


}

export default UserForm


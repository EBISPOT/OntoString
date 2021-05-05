
import { Button, CircularProgress, createStyles, darken, FormGroup, Grid, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { ChangeEvent } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Project from "../../dto/Project";
import ProjectList from "./ProjectList";

interface Props {
    project:Project
    onUpdateProject:(project:Project)=>void
}

interface State {
}

class ProjectForm extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

    }

    render() {

        return <form noValidate autoComplete='off'>
            <FormGroup>
                <Grid container direction="column" spacing={2}>
                    <Grid item>
                        <TextField label="Name" fullWidth variant="outlined" onChange={this.onChangeName} />
                    </Grid>
                    <Grid item>
                        <TextField fullWidth multiline rows={5} variant="outlined" label="Description" onChange={this.onChangeDescription} />
                    </Grid>
                </Grid>
            </FormGroup>
        </form>
    }

    onChangeName = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateProject({ ...this.props.project, name: e.target.value })
    }

    onChangeDescription = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateProject({ ...this.props.project, description: e.target.value })
    }

}

export default ProjectForm


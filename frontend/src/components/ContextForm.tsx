
import { Button, CircularProgress, createStyles, darken, FormGroup, Grid, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { ChangeEvent } from "react";
import { useState, useEffect } from "react";
import Context from "../dto/Context";

interface Props {
    context:Context
    onUpdateContext:(context:Context)=>void
}

interface State {
}

class ContextForm extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

    }

    render() {

        return <form noValidate autoComplete='off'>
            <FormGroup>
                <Grid container direction="column">
                    <Grid item>
                        <TextField label="Name" fullWidth onChange={this.onChangeName} />
                    </Grid>
                    <Grid item>
                        <TextField label="Description" fullWidth onChange={this.onChangeDescription} />
                    </Grid>
                </Grid>
            </FormGroup>
        </form>
    }

    onChangeName = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateContext({ ...this.props.context, name: e.target.value })
    }

    onChangeDescription = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateContext({ ...this.props.context, description: e.target.value })
    }

}

export default ContextForm


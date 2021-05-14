
import { Button, CircularProgress, createStyles, darken, FormGroup, Grid, lighten, makeStyles, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { ChangeEvent } from "react";
import { useState, useEffect } from "react";
import Source from "../../dto/Source";

interface Props {
    source:Source
    onUpdateSource:(source:Source)=>void
}

interface State {
}

class SourceForm extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

    }

    render() {

        let { source } = this.props

        return <form noValidate autoComplete='off'>
            <FormGroup>
                <Grid container direction="column">
                    <Grid item>
                        <TextField label="Name" fullWidth onChange={this.onChangeName} value={source.name}  />
                    </Grid>
                    <Grid item>
                        <TextField label="Description" fullWidth onChange={this.onChangeDescription} value={source.description} />
                    </Grid>
                    <Grid item>
                        <br/>
                        <Select label="Type" fullWidth onChange={this.onChangeType} value={source.type}>
                            <MenuItem value="LOCAL">Local</MenuItem>
                            <MenuItem value="REMOTE">Remote</MenuItem>
                        </Select>
                    </Grid>
                    {source.type === 'REMOTE' &&
                        <Grid item>
                            <TextField label="URL" fullWidth onChange={this.onChangeURI} value={source.uri} />
                        </Grid>
                    }
                </Grid>
            </FormGroup>
        </form>
    }

    onChangeName = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateSource({ ...this.props.source, name: e.target.value })
    }

    onChangeDescription = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateSource({ ...this.props.source, description: e.target.value })
    }

    onChangeType = (e:any) => {
        this.props.onUpdateSource({ ...this.props.source, type: e.target.value as any })
    }

    onChangeURI = (e:ChangeEvent<HTMLInputElement>) => {
        this.props.onUpdateSource({ ...this.props.source, uri: e.target.value })
    }

}

export default SourceForm


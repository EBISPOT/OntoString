
import { Box, Button, CircularProgress, createStyles, darken, Dialog, DialogActions, DialogContent, DialogContentText, DialogTitle, FormGroup, Grid, lighten, makeStyles, MenuItem, Paper, Select, Tab, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tabs, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import React, { Fragment } from "react";
import { useState, useEffect } from "react";
import { get, post } from "../../api";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Source from "../../dto/Source";

interface Props {
    onCancel:()=>void
    projectId:string
    open:boolean
}

interface State {
    tab:string
    name:string
    description:string
    file:any
    loading:boolean
    sources:Source[]|null
    sourceId:string|null
}

class UploadDialog extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            tab: 'NEW',
            name: '',
            description: '',
            file:null,
            loading: false,
            sources: null,
            sourceId: null
        }

    }

    render() {

        let { open } = this.props
        let { tab, name, description, file, sources, sourceId } = this.state

        let canSubmit = tab === 'new' ?
            ( name && description && file ) :
            ( file && sourceId )

        return <Dialog open={open} onClose={this.onCancel}>
                <DialogTitle>Upload CSV/JSON</DialogTitle>
                <DialogContent>
                    <Tabs value={this.state.tab} onChange={this.changeTab}>
                        <Tab label="New Datasource" value={'NEW'} />
                        <Tab label="Existing Datasource" value='EXISTING' />
                    </Tabs>
                    { 
                        tab === 'NEW' &&
                        <Box m={2}>
                        <FormGroup>
                            <Grid container direction="column" spacing={2}>
                                <Grid item>
                                    <TextField label="Name" value={name} variant="outlined" style={{ minWidth: '400px' }} onChange={this.onChangeName} />
                                </Grid>
                                <Grid item>
                                    <TextField label="Description" multiline={true} rows={4} value={description} variant="outlined" style={{ minWidth: '400px' }} onChange={this.onChangeDescription} />
                                </Grid>
                            </Grid>
                        </FormGroup>
                        </Box>
                    }
                    { 
                        tab === 'EXISTING' &&
                        <Box m={2}>
                            <Select fullWidth value={sourceId}>
                                {
                                    sources && sources.map(s => <MenuItem value={s.id}>{s.name}</MenuItem>)
                                }
                            </Select>
                        </Box>
                    }
                    <Box m={2}>
                        {
                            file ?
                            <Fragment>
                            <b>{file.name}</b>
                            <Button onClick={this.deselectFile}>x</Button>
                            </Fragment>
                            :
                        <Button onClick={this.onClickSelectFile} color="primary" variant="outlined">
                            Select File
                        </Button>
                        }
                    </Box>
                </DialogContent>
                <DialogActions>
                    <Button onClick={this.onCancel} color="secondary" variant="outlined">
                        Cancel
                    </Button>
                    <Button onClick={this.onSubmit} color="primary" variant="outlined" disabled={!canSubmit}>
                        Submit
                    </Button>
                </DialogActions>
            </Dialog>
    }

    componentDidMount() {
        this.fetchSources()
    }

    async fetchSources() {

        let { projectId } = this.props

        await this.setState(prevState => ({ ...prevState, loading: true }))

        let sources = await get<Source[]>(`/v1/projects/${projectId}/sources`)

        this.setState(prevState => ({ ...prevState, sources, sourceId: sources[0]?.id||null, loading: false }))

    }

    onCancel = () => {
        this.props.onCancel();
    }

    onSubmit = async () => {

        let { projectId } = this.props

        if(this.state.tab === 'NEW') {

            let source:Source = {
                name: this.state.name,
                description: this.state.description,
                uri: 'file:///' + this.state.file.name,
                type: 'LOCAL'
            }

            let res = await post<Source>(`/v1/projects/${projectId}/sources`, source)

            let uploadRes = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${projectId}/sources/${res.id}/upload`, {
                headers: { ...getAuthHeaders() },
                body: this.state.file
            })

        } else {

            let uploadRes = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${projectId}/sources/${this.state.sourceId}/upload`, {
                headers: { ...getAuthHeaders() },
                body: this.state.file
            })

        }

    }

    onChangeName = (e:any) => {
        this.setState(prevState => ({ ...prevState, name: e.target.value as string }))
    }
    onChangeDescription = (e:any) => {
        this.setState(prevState => ({ ...prevState, description: e.target.value as string }))
    }

    changeTab = (e:any, tab:string) => {
        this.setState(prevState => ({...prevState, tab}))
    }


    onClickSelectFile = () => {

        let input = document.createElement('input') as HTMLInputElement
        input.type = 'file'
        input.accept = '.csv,.json'

        input.onchange = async (e: any) => {
            this.setState(prevState => ({ ...prevState, file: e.target.files[0] }))
        }

        input.click()
    }

    deselectFile = () => {
        this.setState(prevState => ({ ...prevState, file: null }))
    }
}

export default UploadDialog

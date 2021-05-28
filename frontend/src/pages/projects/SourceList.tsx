
import { Button, CircularProgress, createStyles, darken, IconButton, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Theme, WithStyles, withStyles } from "@material-ui/core";
import React from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Source from "../../dto/Source";
import CreateSourceDialog from "./CreateSourceDialog";
import { Link, Redirect } from 'react-router-dom'
import formatDate from "../../formatDate";
import Spinner from "../../components/Spinner";
import { Settings } from "@material-ui/icons";
import Project from "../../dto/Project";
import { get, post } from "../../api";

const styles = (theme:Theme) => createStyles({
    tableRow: {
        // "&": {
        //     cursor: 'pointer'
        // },
        // "&:hover": {
        //     backgroundColor: lighten(theme.palette.primary.light, 0.85)
        // }
    }
})

interface Props extends WithStyles<typeof styles> {
    project:Project
    onCreateSource:()=>void
}

interface State {
    loading:boolean
    sources:Source[]|null
    goToSource:Source|null
}

class SourceList extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            sources: [],
            loading: true,
            goToSource: null
        }
    }

    componentDidMount() {
        this.fetch()
    }

    render() {

        let { goToSource, sources, loading } = this.state
        let { classes, project } = this.props

        if(goToSource !== null) {
            return <Redirect to={`/projects/${project.id}/sources/${goToSource.name}`} />
        }

        if(loading || !sources) {
            return <Spinner/>
        }

        return <TableContainer component={Paper}>
        <Table size="small" aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell align="left">Description</TableCell>
              {/* <TableCell align="left">Created by</TableCell>
              <TableCell align="left">Created</TableCell> */}
              <TableCell align="left"></TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {sources.map((source:Source) => (
              <TableRow className={classes.tableRow} key={source.name}>
                <TableCell component="th" scope="row">
                    {source.name}
                </TableCell>
                <TableCell align="left">
                    {source.description}
                </TableCell>
                {/* <TableCell align="left">
                    {source.created!.user.name}
                </TableCell>
                <TableCell align="left">
                    {formatDate(source.created!.timestamp)}
                </TableCell> */}
                <TableCell align="left">
                    {/* <IconButton onClick={(e) => { e.preventDefault(); e.stopPropagation(); this.onClickSourceSettings(source) }}>
                        <Settings />
                    </IconButton> */}
                </TableCell>
              </TableRow>
            ))}
            <TableRow>
                <TableCell colSpan={3} align="right">
                    <CreateSourceDialog onCreate={this.createSource} />
                </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    }

    async fetch() {

        let { project } = this.props

        await this.setState(prevState => ({ ...prevState, loading: true }))

        let sources = await get<Source[]>(`/v1/projects/${project.id}/sources`)

        this.setState(prevState => ({ ...prevState, sources, loading: false }))
    }

    onClickSourceSettings = async (source:Source) => {
        this.setState(prevState => ({ ...prevState, goToSource: source }))
    }

    createSource = async (source: Source) => {

        let { project } = this.props

        await post<Source>(`/v1/projects/${project.id}/sources`, source)

        this.props.onCreateSource()

        this.setState(prevState => ({ ...prevState, showCreateSourceDialog: false }))
    }


}

export default withStyles(styles)(SourceList)


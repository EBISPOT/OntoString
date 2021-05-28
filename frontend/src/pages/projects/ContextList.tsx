
import { Button, CircularProgress, createStyles, darken, IconButton, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Theme, WithStyles, withStyles } from "@material-ui/core";
import React from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Context from "../../dto/Context";
import CreateContextDialog from "./CreateContextDialog";
import { Link, Redirect } from 'react-router-dom'
import formatDate from "../../formatDate";
import Spinner from "../../components/Spinner";
import { Settings } from "@material-ui/icons";
import Project from "../../dto/Project";
import { post } from "../../api";

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
    onCreateContext:()=>void
}

interface State {
    goToContext:Context|null
}

class ContextList extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            goToContext: null,
        }


    }

    render() {

        let { goToContext } = this.state
        let { classes, project } = this.props

        if(goToContext !== null) {
            return <Redirect to={`/projects/${project.id}/contexts/${goToContext.name}`} />
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
            {project.contexts.map((context:Context) => (
              <TableRow className={classes.tableRow} key={context.name}>
                <TableCell component="th" scope="row">
                    {context.name}
                </TableCell>
                <TableCell align="left">
                    {context.description}
                </TableCell>
                {/* <TableCell align="left">
                    {context.created!.user.name}
                </TableCell>
                <TableCell align="left">
                    {formatDate(context.created!.timestamp)}
                </TableCell> */}
                <TableCell align="left">
                    <IconButton onClick={(e) => { e.preventDefault(); e.stopPropagation(); this.onClickContextSettings(context) }}>
                        <Settings />
                    </IconButton>
                </TableCell>
              </TableRow>
            ))}
            <TableRow>
                <TableCell colSpan={3} align="right">
                    <CreateContextDialog onCreate={this.createContext} />
                </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    }

    onClickContextSettings = async (context:Context) => {
        this.setState(prevState => ({ ...prevState, goToContext: context }))
    }

    createContext = async (context: Context) => {

        let { project } = this.props

        await post<Context>(`/v1/projects/${project.id}/contexts`, context)

        this.props.onCreateContext()

        this.setState(prevState => ({ ...prevState, showCreateContextDialog: false }))
    }


}

export default withStyles(styles)(ContextList)


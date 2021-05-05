
import { AppBar, Breadcrumbs, CircularProgress, FormGroup, Grid, Link, Paper, Tab, Tabs, TextField, Typography } from "@material-ui/core";
import React, { Fragment } from "react";
import { Redirect } from "react-router-dom";
import { get, post } from "../../api";
import { getAuthHeaders, isLoggedIn } from "../../auth";
import Provenance from "../../components/Provenance";
import Context from "../../dto/Context";
import Project from "../../dto/Project";
import EntityList from "../entities/EntityList";
import { Link as RouterLink } from 'react-router-dom'
import ContextSelector from "../../components/ContextSelector";
import Header from "../../components/Header";
import Spinner from "../../components/Spinner";
import ContextList from "./ContextList";

interface Props {
    projectId:string
}

interface State {
    project:Project|null
    context:Context|null,
    showCreateContextDialog: boolean
}

export default class ProjectSettingsPage extends React.Component<Props, State> {

    constructor(props:Props) {
        super(props)

        this.state = {
            project: null,
            context: null,
            showCreateContextDialog: false
        }
    }

    componentDidMount() {
        this.fetchProject()
    }

    render() {

        let { project, showCreateContextDialog } = this.state

        if (!isLoggedIn()) {
            return <Redirect to='/login' />
        }

        if(project === null) {
            return <Fragment>
                <Header section='projects' projectId={this.props.projectId} />
                <main> <Spinner /> </main>
            </Fragment >
        }

        return <Fragment>
            <Header section='projects' projectId={this.props.projectId} />
            <main>
            <Breadcrumbs>
                <Link color="inherit" component={RouterLink} to="/">
                    Projects
                </Link>
                <Typography color="textPrimary">{project.name}</Typography>
            </Breadcrumbs>
            <h1>{project.name}</h1>
            { project.created && <Provenance provenance={project.created} label="Created by" /> }

            <br/>

                <form noValidate autoComplete='off'>
                    <FormGroup>
                        <Grid container direction="column" spacing={2}>
                            <Grid item>
                                <TextField fullWidth variant="outlined" label="Name" value={project.name} />
                            </Grid>
                            <Grid item>
                                <TextField multiline fullWidth variant="outlined" rows={4} label="Description" value={project.description} />
                            </Grid>
                        </Grid>
                    </FormGroup>
                </form>

            <br/>

            <h2>Contexts</h2>
            <ContextList project={project} onCreateContext={() => this.fetchProject()} />

        </main>
        </Fragment>
    }

    async fetchProject() {

        let { projectId } = this.props

        let project = await get<Project>(`/v1/projects/${projectId}`)

        this.setState(prevState => ({ ...prevState, project }))
    }

    
}

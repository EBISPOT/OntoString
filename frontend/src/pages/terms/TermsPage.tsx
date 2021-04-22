
import { AppBar, Breadcrumbs, CircularProgress, Link, Paper, Tab, Tabs, Typography } from "@material-ui/core";
import React, { Fragment } from "react";
import { Redirect } from "react-router-dom";
import { get, post } from "../../api";
import { getAuthHeaders, isLoggedIn } from "../../auth";
import Provenance from "../../components/Provenance";
import Context from "../../dto/Context";
import Project from "../../dto/Project";
import TermList from "../terms/TermList";
import { Link as RouterLink } from 'react-router-dom'
import ContextSelector from "../../components/ContextSelector";
import Header from "../../components/Header";
import Spinner from "../../components/Spinner";

interface Props {
    projectId:string
}

interface State {
    project:Project|null
    context:Context|null,
    showCreateContextDialog: boolean
}

export default class TermsPage extends React.Component<Props, State> {

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
                <Header section='terms' projectId={this.props.projectId} />
                <main> <Spinner /> </main>
            </Fragment >
        }

        return <Fragment>
            <Header section='terms' projectId={this.props.projectId} />
            <main>
            <Breadcrumbs>
                <Link color="inherit" component={RouterLink} to="/">
                    Projects
                </Link>
                <Link color="inherit" component={RouterLink} to={`/projects/${project.id!}`}>
                    {project.name}
                </Link>
                <Typography color="textPrimary">Terms</Typography>
            </Breadcrumbs>
            <h1>{project.name}</h1>
            <Typography variant='subtitle1'>{project.description}</Typography>
            { project.created && <Provenance provenance={project.created} label="Created by" /> }

            <br/>
            {/* <AppBar position="static"> */}
                {/* </AppBar> */}
                        <h2>Terms</h2>
                        <TermList project={project!} />
        </main>
        </Fragment>
    }

    async fetchProject() {

        let { projectId } = this.props

        let project = await get<Project>(`/v1/projects/${projectId}`)

        this.setState(prevState => ({ ...prevState, project }))
    }

    
}

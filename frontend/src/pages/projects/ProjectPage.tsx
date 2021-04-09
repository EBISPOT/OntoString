
import { AppBar, Breadcrumbs, CircularProgress, Link, Paper, Tab, Tabs, Typography } from "@material-ui/core";
import React from "react";
import { Redirect } from "react-router-dom";
import { get, post } from "../../api";
import { getAuthHeaders, isLoggedIn } from "../../auth";
import Provenance from "../../components/Provenance";
import Context from "../../dto/Context";
import Project from "../../dto/Project";
import EntityList from "../entities/EntityList";
import CreateContextDialog from "./CreateContextDialog";
import { Link as RouterLink } from 'react-router-dom'

interface Props {
    id:string
}

interface State {
    project:Project|null
    context:Context|null,
    showCreateContextDialog: boolean
}

export default class ProjectPage extends React.Component<Props, State> {

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

        let { project, context, showCreateContextDialog } = this.state

        if (!isLoggedIn()) {
            return <Redirect to='/login' />
        }

        if(project === null || context === null) {
            return <CircularProgress />
        }

        return <div>
            { showCreateContextDialog && <CreateContextDialog onCreate={this.createContext} onClose={this.closeCreateContext} /> }
            <Breadcrumbs>
                <Link color="inherit" component={RouterLink} to="/">
                    Projects
                </Link>
                <Typography color="textPrimary">{project.name}</Typography>
            </Breadcrumbs>
            { project.created && <Provenance provenance={project.created} label="Created by" /> }
            <h1>{project.name}</h1>
            <Typography variant='subtitle1'>{project.description}</Typography>
            <br/>
            {/* <AppBar position="static"> */}
            <Tabs
                indicatorColor="primary"
                textColor="primary"
                value={context.name}
                // onChange={handleChange}
            >
                {project.contexts.map(c => <Tab value={c.name} label={c.name} onClick={() => this.setContext(c)} />)}
                <Tab label={"+ New Context"} onClick={this.newContext} />
            </Tabs>
                {/* </AppBar> */}
                        <h2>Entities</h2>
                        <EntityList projectId={project!.id as string} contextId={context!.name as string} />
        </div>
    }

    async fetchProject() {

        let { id } = this.props

        let project = await get<Project>(`/v1/projects/${id}`)

        let context = project.contexts.filter(c => c.name === 'DEFAULT')[0]

        if(this.state.context) {
            let currentContextName = this.state.context.name
            let currentContext = project.contexts.filter(c => c.name === currentContextName)[0]
            if(currentContext) {
                context = currentContext
            }
        }
        this.setState(prevState => ({ ...prevState, project, context }))
    }
    
    newContext = () => {
        this.setState(prevState => ({ ...prevState, showCreateContextDialog: true }))
    }

    createContext = async (context:Context) => {

        let { id } = this.props

        await post<Context>(`/v1/projects/${id}/contexts`, context)

        this.fetchProject()

        this.setState(prevState => ({ ...prevState, showCreateContextDialog: false }))
    }

    closeCreateContext = () => {
        this.setState(prevState => ({ ...prevState, showCreateContextDialog: false }))
    }

    setContext = (context:Context) => {
        this.setState(prevState => ({ ...prevState, context }))
    }
}

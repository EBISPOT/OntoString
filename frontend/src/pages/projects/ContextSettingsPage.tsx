
import { AppBar, Box, Breadcrumbs, Checkbox, Chip, CircularProgress, debounce, FormControlLabel, FormGroup, Grid, Link, Paper, Tab, Tabs, TextField, Typography } from "@material-ui/core";
import React, { Fragment } from "react";
import { Redirect } from "react-router-dom";
import { get, post, put } from "../../api";
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
import GraphRestrictionClassDialog from "./GraphRestrictionClassDialog";
import LoadingOverlay from "../../components/LoadingOverlay";

interface Props {
    projectId:string
    contextName:string
}

interface State {
    project:Project|null
    context:Context|null,
    saving:boolean
    showAddGraphRestrictionClassDialog: boolean
}

export default class ContextSettingsPage extends React.Component<Props, State> {

    constructor(props:Props) {
        super(props)

        this.state = {
            project: null,
            context: null,
            saving: false,
            showAddGraphRestrictionClassDialog: false
        }
    }

    componentDidMount() {
        this.fetch()
    }

    render() {

        let { project, context, showAddGraphRestrictionClassDialog, saving } = this.state

        if (!isLoggedIn()) {
            return <Redirect to='/login' />
        }

        if(project === null || context === null) {
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
                <Link color="inherit" component={RouterLink} to={"/projects/" + project.id + '/settings'}>
                    {project.name}
                </Link>
                <Link color="inherit" component={RouterLink} to={"/projects/" + project.id + '/settings'}>
                    Contexts
                </Link>
                <Typography color="textPrimary">{context.name}</Typography>
            </Breadcrumbs>
            <h1>{context.name}</h1>
            { context.created && <Provenance provenance={context.created} label="Created by" /> }

            <LoadingOverlay active={saving}>

            <GraphRestrictionClassDialog open={showAddGraphRestrictionClassDialog} onSubmit={this.addGraphRestrictionClass} onCancel={this.closeAddGraphRestrictionClass} />

            <br/>

                <form noValidate autoComplete='off'>
                    <FormGroup>
                        <Grid container direction="column" spacing={2}>
                            {/* <Grid item>
                                <TextField fullWidth variant="outlined" label="Name" value={context.name} onChange={this.changeName} />
                            </Grid> */}
                            <Grid item>
                                <TextField multiline fullWidth variant="outlined" rows={4} label="Description" value={context.description} onChange={this.changeDescription} />
                            </Grid>
                        </Grid>
                    </FormGroup>
                </form>

            <br/>


            {/* <Paper>
                <Box m={2}> */}
            <h2>Graph Restriction</h2>

            <p>
                Graph restriction can limit mappings within this context to specific subsets of ontologies. For example, a context for diseases might be restricted to mapping to terms that are subclasses of <code>MONDO:0000001</code> (&quot;disease or disorder&quot;).
            </p>

            <Fragment>

                <h3>Parent Classes</h3>
                <Box m={2}>
                <p>
                    {context.graphRestriction && context.graphRestriction.classes.map((curie) => {
                        return (
                                <Chip
                                    label={curie}
                                    onDelete={() => { this.removeGraphRestrictionClass(curie) }}
                                    color="primary"
                                    variant="outlined"
                                />
                        );
                    })}
                    &nbsp;
                    <Chip
                        label={<b>+ Add Class</b>}
                        color="primary"
                        variant="outlined"
                        onClick={this.clickAddGraphRestrictionClass}
                    />
                    </p>
                    </Box>

                <h3>Refinements</h3>
                <Box m={2}>
                <p>
                    Check <b>Direct</b> to exclude terms that are not direct subclasses. For example, a subclass of a subclass would not be included in the restricted set of terms.
                </p>
                <p>
                    <FormControlLabel
                        control={
                            <Checkbox
                                onChange={this.toggleDirect}
                                checked={context.graphRestriction && context.graphRestriction.direct}
                                name="checkedB"
                                color="primary"
                            />
                        }
                        label="Direct"
                    />
                </p>
                <p>
                    Check <b>Include Self</b> to include the specified class(es) themselves in the restricted set of terms. For example, if the class is <code>MONDO:0000001</code> and Include Self is checked, a mapping to <code>MONDO:0000001</code> would be valid.
                </p>
                <p>
                    <FormControlLabel
                        control={
                            <Checkbox
                                onChange={this.toggleIncludeSelf}
                                checked={context.graphRestriction && context.graphRestriction.include_self}
                                name="checkedB"
                                color="primary"
                            />
                        }
                        label="Include Self"
                    />
                </p>
            </Box>

            </Fragment>

</LoadingOverlay>
        </main>
        </Fragment>
    }

    async fetch() {

        let { projectId, contextName } = this.props

        let project = await get<Project>(`/v1/projects/${projectId}`)

        let context = project.contexts.filter(c => c.name === contextName)[0]

        if(!context) {
            console.dir(project)
            throw new Error('Context not found: ' + contextName)
        }

        this.setState(prevState => ({ ...prevState, project, context }))
    }

    async save() {

        let { projectId, contextName } = this.props


        console.log('saving')
        console.log(this.state.context)

        await this.setState(prevState => ({ ...prevState, saving: true }))
        await put<Context>(`/v1/projects/${projectId}/contexts`, this.state.context!)
        await this.fetch()
        await this.setState(prevState => ({ ...prevState, saving: false }))
    }

    saveWithDelay = debounce(async () => this.save(), 1000)


    clickAddGraphRestrictionClass = () => {
        this.setState(prevState => ({ ...prevState, showAddGraphRestrictionClassDialog: true }))
    }

    closeAddGraphRestrictionClass = () => {
        this.setState(prevState => ({ ...prevState, showAddGraphRestrictionClassDialog: false }))
    }

    removeGraphRestrictionClass = async (curie:string) => {

        let context = this.state.context!
        let graphRestriction = context.graphRestriction!

        let newContext = {
            ...context,
            graphRestriction: {
                ...graphRestriction,
                classes: graphRestriction!.classes.filter(c => c !== curie)
            }
        }

        await this.setState(prevState => ({ ...prevState, context: newContext }))
        await this.save()
    }

    // changeName = (e:any) => {

    //     let name = e.target.value 

    //     this.setState(prevState => ({ ...prevState, context: { ...prevState.context!, name } }))
    //     this.save()
    // }

    changeDescription =  async (e:any) => {
        let description = e.target.value 

        await this.setState(prevState => ({ ...prevState, context: { ...prevState.context!, description } }))
        await this.saveWithDelay()
    }

    addGraphRestrictionClass = async (curie:string) => {

        let context = this.state.context!
        let graphRestriction = context.graphRestriction || newGraphRestriction()

        let newContext = {
            ...context,
            graphRestriction: {
                ...graphRestriction,
                classes: graphRestriction!.classes.concat([ curie ])
            }
        }

        console.log(newContext.graphRestriction.classes)

        await this.setState(prevState => ({ ...prevState, context: newContext }))
        await this.save()
    }

    toggleDirect = async () => {

        let context = this.state.context!
        let graphRestriction = context.graphRestriction || newGraphRestriction()

        let newContext = {
            ...context,
            graphRestriction: {
                ...graphRestriction,
                direct: !graphRestriction.direct
            }
        }

        await this.setState(prevState => ({ ...prevState, context: newContext }))
        await this.save()
    }

    toggleIncludeSelf = async () => {

        let context = this.state.context!
        let graphRestriction = context.graphRestriction || newGraphRestriction()

        let newContext = {
            ...context,
            graphRestriction: {
                ...graphRestriction,
                include_self: !graphRestriction.include_self
            }
        }

        await this.setState(prevState => ({ ...prevState, context: newContext }))
        await this.save()
    }

}


function newGraphRestriction() {
    return {
        classes: [],
        relations: [ 'rdfs:subClassOf' ],
        direct: false,
        include_self: false
    }
}
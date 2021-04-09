
import { Box, Breadcrumbs, Button, CircularProgress, Grid, Link, Paper, TextField, Typography } from "@material-ui/core";
import { Search, Add } from '@material-ui/icons'
import React from "react";
import { Redirect } from "react-router-dom";
import { post, get, put } from "../../api";
import { getAuthHeaders, isLoggedIn } from "../../auth";
import AuditTrail from "../../components/AuditTrail";
import EntityStatusBox from "../../components/EntityStatusBox";
import MappingStatusBox from "../../components/MappingStatusBox";
import Entity from "../../dto/Entity";
import Mapping, { CreateMapping, MappingStatus } from "../../dto/Mapping";
import MappingSuggestion from "../../dto/MappingSuggestion";
import OntologyTerm from "../../dto/OntologyTerm";
import Project from "../../dto/Project";
import MappingSuggestionList from "./MappingSuggestionList";
import MappingTermList from "./MappingTermList";
import { Link as RouterLink } from 'react-router-dom'

interface Props {
    projectId:string
    entityId:string
}

interface State {
    project:Project|null
    entity:Entity|null
    saving:boolean
}

export default class EntityPage extends React.Component<Props, State> {

    constructor(props:Props) {
        super(props)

        this.state = {
            entity: null,
            project: null,
            saving: false
        }
    }

    componentDidMount() {
        this.fetch()
    }

    render() {

        let { project, entity, saving } = this.state

        if (!isLoggedIn()) {
            return <Redirect to='/login' />
        }

        if(!project || !entity) {
            return <CircularProgress />
        }

        return <div>
            <Breadcrumbs>
                <Link color="inherit" component={RouterLink} to="/">
                    Projects
                </Link>
                <Link color="inherit" component={RouterLink} to={`/projects/${project.id!}`}>
                    {project.name}
                </Link>
                <Link color="inherit" component={RouterLink} to={`/projects/${project.id!}`}>
                    Entities
                </Link>
                <Typography color="textPrimary">{entity.name}</Typography>
            </Breadcrumbs>
            <h1>{entity.name} <EntityStatusBox status={entity.mappingStatus} /></h1>
            {/* <h2>Mappings</h2>
            <MappingTermList project={project} entity={entity} onRemoveMappingTerm={this.onRemoveMappingTerm} /> */}
            <h2>Suggested Mappings</h2>
            <MappingSuggestionList project={project} entity={entity} saving={saving} onClickSuggestion={this.onClickSuggestion} />
            <br/>
            <Button variant="outlined" size="large" color="primary" startIcon={<Search />}>Search Ontologies...</Button>
            &nbsp;
            &nbsp;
            <Button variant="outlined" size="large" color="primary" startIcon={<Add />}>Propose New Term...</Button>

            <h2>History</h2>
            <AuditTrail trail={entity.auditTrail} />
        </div>
    }

    async fetch() {

        let { projectId, entityId } = this.props

        //await this.setState(prevState => ({ ...prevState, project: null, entity: null }))

        let [ project, entity ] = await Promise.all([
            get<Project>(`/v1/projects/${projectId}`),
            get<Entity>(`/v1/projects/${projectId}/entities/${entityId}`)
        ])

        this.setState(prevState => ({ ...prevState, project, entity }))
    }

    onClickSuggestion = (suggestion:MappingSuggestion) => {

        let entity = this.state.entity!

        let term = suggestion.ontologyTerm

        if(entity.mapping) {

            let wasSelected = entity.mapping.ontologyTerms.filter(t => t.iri === term.iri).length > 0

            if(wasSelected) {

                this.updateMapping({
                    ...entity.mapping,
                    ontologyTerms: entity.mapping.ontologyTerms.filter(t => t.iri !== term.iri)
                })

            } else {

                this.updateMapping({
                    ...entity.mapping,
                    ontologyTerms: [
                        ...entity.mapping.ontologyTerms,
                        suggestion.ontologyTerm
                    ]
                })
                
            }

        } else {

            this.createMapping({
                entityId: entity.id,
                ontologyTerms: [
                    suggestion.ontologyTerm
                ]
            })

        }


    }

    private async createMapping(mapping:CreateMapping) {

        let { projectId, entityId } = this.props

        await this.setState(prevState => ({ ...prevState, saving: true }))
        await post(`/v1/projects/${projectId}/mappings`, mapping)
        await this.fetch()
        await this.setState(prevState => ({ ...prevState, saving: false }))
    }

    private async updateMapping(mapping:Mapping) {

        let { projectId, entityId } = this.props

        await this.setState(prevState => ({ ...prevState, saving: true }))
        await put(`/v1/projects/${projectId}/mappings/${mapping.id}`, mapping)
        await this.fetch()
        await this.setState(prevState => ({ ...prevState, saving: false }))
    }

    onRemoveMappingTerm = (term:OntologyTerm) => {
    }
}

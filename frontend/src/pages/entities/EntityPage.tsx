
import { Box, Breadcrumbs, Button, CircularProgress, Grid, Link, Paper, TextField, Typography } from "@material-ui/core";
import { Search, Add } from '@material-ui/icons'
import React, { Fragment } from "react";
import { Redirect } from "react-router-dom";
import { post, get, put, request } from "../../api";
import { getAuthHeaders, isLoggedIn } from "../../auth";
import AuditTrail from "../../components/AuditTrail";
import EntityStatusBox from "../../components/EntityStatusBox";
import MappingStatusBox from "../../components/MappingStatusBox";
import Entity from "../../dto/Entity";
import Mapping, { CreateMapping, MappingStatus } from "../../dto/Mapping";
import MappingSuggestion from "../../dto/MappingSuggestion";
import OntologyTerm, { OntologyTermStatus } from "../../dto/OntologyTerm";
import Project from "../../dto/Project";
import MappingSuggestionList from "./MappingSuggestionList";
import MappingTermList from "./MappingTermList";
import { Link as RouterLink } from 'react-router-dom'
import SearchOntologiesDialog from "./SearchOntologiesDialog";
import { OlsSearchResult } from "../../dto/OlsSearchResult";
import ReviewList from "./ReviewList";
import CommentList from "./CommentList";
import AddReviewDialog from "./AddReviewDialog";
import AddCommentDialog from "./AddCommentDialog";

interface Props {
    projectId:string
    entityId:string
}

interface State {
    project:Project|null
    entity:Entity|null
    saving:boolean
    showSearchOntologiesDialog:boolean
    showAddReviewDialog:boolean
    showAddCommentDialog:boolean
}

export default class EntityPage extends React.Component<Props, State> {

    constructor(props:Props) {
        super(props)

        this.state = {
            entity: null,
            project: null,
            saving: false,
            showSearchOntologiesDialog: false,
            showAddReviewDialog: false,
            showAddCommentDialog: false
        }
    }

    componentDidMount() {
        this.fetch()
    }

    render() {

        let { project, entity, saving, showSearchOntologiesDialog, showAddReviewDialog, showAddCommentDialog } = this.state

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


            <SearchOntologiesDialog open={showSearchOntologiesDialog} onClose={this.closeSearchOntologies} onSelectTerm={this.onSelectOntologyTerm} project={project} />
            <AddReviewDialog open={showAddReviewDialog} onCancel={this.closeAddReview} onSubmit={this.onAddReview} />
            <AddCommentDialog open={showAddCommentDialog} onCancel={this.closeAddComment} onSubmit={this.onAddComment} />



            <h1>{entity.name} <EntityStatusBox status={entity.mappingStatus} /></h1>
            {/* <h2>Mappings</h2>
            <MappingTermList project={project} entity={entity} onRemoveMappingTerm={this.onRemoveMappingTerm} /> */}
            <h2>Suggested Mappings</h2>
            <MappingSuggestionList project={project} entity={entity} saving={saving} onClickSuggestion={this.onClickSuggestion} />
            <br/>
            <Button variant="outlined" size="large" color="primary" startIcon={<Search />} onClick={this.openSearchOntologies}>Search Ontologies...</Button>
            &nbsp;
            &nbsp;
            <Button variant="outlined" size="large" color="primary" startIcon={<Add />}>Propose New Term...</Button>

            <h2>History</h2>
            <AuditTrail trail={entity.auditTrail} />

            <h2>Reviews</h2>
            { !entity.mapping && <p><i>Create a mapping to enable reviews</i></p> }
            { entity.mapping && 
                <Fragment>
                    <ReviewList mapping={entity.mapping} />
                    <Button variant="outlined" color="primary" size="large" onClick={this.onClickAddReview}>+ Add Review</Button>
                </Fragment>
                }

            <h2>Comments</h2>
            { !entity.mapping && <p><i>Create a mapping to enable comments</i></p> }
            { entity.mapping && 
                <Fragment>
                    <CommentList mapping={entity.mapping} />
                    <Button variant="outlined" color="primary" size="large" onClick={this.onClickAddComment}>+ Add Comment</Button>
                </Fragment>
                }

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

    openSearchOntologies = async () => {

        console.log('sso')
        await this.setState(prevState => ({ ...prevState, showSearchOntologiesDialog: true }))
    }

    closeSearchOntologies = async () => {
        await this.setState(prevState => ({ ...prevState, showSearchOntologiesDialog: false }))
    }

    onSelectOntologyTerm = async (olsTerm:OlsSearchResult) => {

        let entity = this.state.entity!

        let term:OntologyTerm = {
            iri: olsTerm.iri,
            curie: olsTerm.obo_id,
            label: olsTerm.label,
            //status: OntologyTermStatus.CURRENT
            //description: '',
            //crossRefs: ''
        } as OntologyTerm

    // curie:string
    // iri:string
    // label:string
    // status:OntologyTermStatus
    // description:string
    // crossRefs:string

        if(entity.mapping) {

            this.updateMapping({
                ...entity.mapping,
                ontologyTerms: [
                    ...entity.mapping.ontologyTerms,
                    term
                ]
            })

        } else {

            this.createMapping({
                entityId: entity.id,
                ontologyTerms: [
                    term
                ]
            })

        }



        await this.setState(prevState => ({ ...prevState, showSearchOntologiesDialog: false }))
    }

    onClickAddReview = () => {
        this.setState(prevState => ({ ...prevState, showAddReviewDialog: true }))
    }

    closeAddReview = () => {
        this.setState(prevState => ({ ...prevState, showAddReviewDialog: false }))
    }

    onAddReview = async (comment:string) => {

        let { projectId, entityId } = this.props

        let { entity } = this.state

        let mapping = entity!.mapping

        await this.setState(prevState => ({ ...prevState, saving: true, showAddReviewDialog: false }))

        await request(`/v1/projects/${projectId}/mappings/${mapping.id}/reviews`, {
            method: 'POST',
            body: comment,
            headers: {
                'content-type': 'text/plain'
            }
        })

        await this.fetch()
        await this.setState(prevState => ({ ...prevState, saving: false }))
    }

    onClickAddComment = () => {
        this.setState(prevState => ({ ...prevState, showAddCommentDialog: true }))
    }

    closeAddComment = () => {
        this.setState(prevState => ({ ...prevState, showAddCommentDialog: false }))
    }

    onAddComment = async (comment:string) => {

        let { projectId, entityId } = this.props

        let { entity } = this.state

        let mapping = entity!.mapping

        await this.setState(prevState => ({ ...prevState, saving: true, showAddCommentDialog: false }))

        await request(`/v1/projects/${projectId}/mappings/${mapping.id}/comments`, {
            method: 'POST',
            body: comment,
            headers: {
                'content-type': 'text/plain'
            }
        })

        await this.fetch()
        await this.setState(prevState => ({ ...prevState, saving: false }))
    }
}

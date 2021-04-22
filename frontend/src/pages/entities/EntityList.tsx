
import { Button, CircularProgress, createStyles, darken, Grid, Input, InputAdornment, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import SearchIcon from '@material-ui/icons/Search';
import React, { ChangeEvent, Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import Entity from "../../dto/Entity";
import { Link, Redirect } from 'react-router-dom'
import DataTable from "react-data-table-component";
import Paginated from "../../dto/Paginated";
import MappingStatusBox from "../../components/MappingStatusBox";
import EntityStatusBox from "../../components/EntityStatusBox";
import Spinner from "../../components/Spinner";
import Context from "../../dto/Context";
import Project from "../../dto/Project";
import ContextSelector from "../../components/ContextSelector";

const styles = (theme:Theme) => createStyles({
    tableRow: {
        "&": {
            cursor: 'pointer'
        },
        "&:hover": {
            backgroundColor: lighten(theme.palette.primary.light, 0.85)
        }
    }
})

interface Props extends WithStyles<typeof styles> {
    project:Project
}

interface State {
    page:number
    size:number
    sortColumn:string
    sortDirection:'asc'|'desc'
    filter:string
    loading:boolean
    entities:Paginated<Entity>|null
    goToEntity:Entity|null
    context:Context
}

class EntityList extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            page: 0,
            size: 20,
            sortColumn: '',
            sortDirection: 'asc',
            filter: '',
            loading:true,
            entities: null,
            goToEntity: null,
            context: props.project.contexts.filter(c => c.name === 'DEFAULT')[0]!
        }

        console.dir(this.state)


    }

    componentDidMount() {
        this.fetchEntities()
    }

    // componentDidUpdate(prevProps:Props){
    //        this.props.projectId !== prevProps.projectId) {
    //                 this.fetchEntities()
    //     }
    // }

    columns: any[] = [
        {
            name: 'Name',
            selector: 'name',
            sortable: true
        },
        {
            name: 'Mapping Status',
            selector: 'mappingStatus',
            sortable: true,
            cell: (entity:Entity) => <EntityStatusBox status={entity.mappingStatus} />
        },
        {
            name: 'Mapping Term',
            selector: 'mapping',
            sortable: true,
            cell: (entity:Entity) => renderEntityMappingTerm(entity)
        },
        {
            name: 'Mapping Label',
            selector: 'mapping',
            sortable: true,
            cell: (entity:Entity) => renderEntityMappingLabel(entity)
        },
        {
            name: 'Source',
            selector: 'source',
            sortable: true,
            ignoreRowClick: true,
            cell: (entity:Entity) => { entity.source && <Link to={`/sources/${entity.source.id}`}>{entity.source.name}</Link> }
        },
        // {
        //     name: 'Mapping Suggestions',
        //     selector: 'mappingSuggestions',
        //     sortable: true
        // },
        // {
        //     name: 'Mapping',
        //     selector: 'mapping',
        //     sortable: true
        // },
        // {
        //     // created
        // }
    ]

    render() {

        let { classes, project } = this.props
        let { entities, goToEntity, context } = this.state

        // if(entities === null) {
        //     return <CircularProgress />
        // }

        if(goToEntity !== null) {
            return <Redirect to={`/projects/${project.id}/entities/${goToEntity.id}`} />
        }

        return <Fragment>
            <Grid container>
                <Grid item xs={6}>
            <Input startAdornment={<InputAdornment position="start"><SearchIcon /></InputAdornment>} onChange={this.onFilter} />
            </Grid>
                <Grid item xs={6} container justify="flex-end">
                <ContextSelector project={project} context={context} onSwitchContext={this.onSwitchContext}  />
            </Grid>
            </Grid>
            <DataTable
                columns={this.columns}
                data={entities?.content || []}
                pagination
                paginationServer
                paginationTotalRows={entities?.totalElements || 0}
                paginationPerPage={this.state.size}
                paginationDefaultPage={this.state.page + 1}
                sortServer
                onSort={this.onSort}
                onChangeRowsPerPage={this.onChangeRowsPerPage}
                onChangePage={this.onChangePage}
                onRowClicked={this.onClickEntity}
                noHeader
                highlightOnHover
                pointerOnHover
                progressPending={this.state.loading}
                progressComponent={<Spinner />}
            />
        </Fragment>
    }

    async fetchEntities() {

        let { project } = this.props
        let { context } = this.state

        let { page, size, sortColumn, sortDirection, filter } = this.state

        await this.setState(prevState => ({ ...prevState, loading: true }))

        let res = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${project.id}/entities?${
            new URLSearchParams({
                context: context!.name!,
                page: page.toString(),
                size: size.toString(),
                ...(sortColumn ? { sort: sortColumn + ',' + sortDirection } : {}),
                search: filter
            })
        }`, {
            headers: { ...getAuthHeaders() }
        })

        let entities:Paginated<Entity> = await res.json()

        this.setState(prevState => ({ ...prevState, entities, loading: false }))
    }

    onClickEntity = async (entity:Entity) => {
        this.setState(prevState => ({ ...prevState, goToEntity: entity }))
    }

    onChangePage = async (page:number) => {
        await this.setState(prevState => ({ ...prevState, page: page-1 }))
        this.fetchEntities()
    }

    onChangeRowsPerPage = async (rowsPerPage:number) => {
        await this.setState(prevState => ({ ...prevState, size: rowsPerPage }))
        this.fetchEntities()
    }
    
    onSort = async (column: any, sortDirection:'asc'|'desc') => {
        await this.setState(prevState => ({ ...prevState, sortColumn: column.selector, sortDirection }))
        this.fetchEntities()
    }

    onFilter = async (e:ChangeEvent<HTMLInputElement>) => {
        await this.setState(prevState => ({ ...prevState, filter: e.target.value }))
        this.fetchEntities()
    }

    // onCreateEntity = async (entity:Entity) => {

    //     let res = await fetch(process.env.REACT_APP_APIURL + '/v1/entities', {
    //         method: 'POST',
    //         headers: { ...getAuthHeaders(), 'content-type': 'application/json' },
    //         body: JSON.stringify(entity)
    //     })

    //     if(res.status === 201) {
    //         this.fetchEntities()
    //     } else {
    //         console.log('Error creating entities: ' + res.statusText)
    //     }
    // }

    onSwitchContext = async (context:Context) => {

        await this.setState(prevState => ({ ...prevState, context }))
        this.fetchEntities()
    }
}

export default withStyles(styles)(EntityList)


function renderEntityMappingTerm(entity:Entity) {
    
    let { mapping } = entity

    if(!mapping)
        return ''

    return mapping.ontologyTerms.map(term => <div>{term.curie}</div>)
}

function renderEntityMappingLabel(entity:Entity) {
    
    let { mapping } = entity

    if(!mapping)
        return ''

    return mapping.ontologyTerms.map(term => <div>{term.label}</div>)
}


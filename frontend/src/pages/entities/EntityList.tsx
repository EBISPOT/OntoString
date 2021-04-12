
import { Button, CircularProgress, createStyles, darken, Input, InputAdornment, lighten, makeStyles, Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
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
    projectId:string
    contextId:string
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
            goToEntity: null
        }


    }

    componentDidMount() {
        this.fetchEntities()
    }

    componentDidUpdate(prevProps:Props){
        if (this.props.contextId !== prevProps.contextId ||
                this.props.projectId !== prevProps.projectId) {
                    this.fetchEntities()
        }
    }

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

        let { entities, goToEntity } = this.state
        let { classes, projectId } = this.props

        // if(entities === null) {
        //     return <CircularProgress />
        // }

        if(goToEntity !== null) {
            return <Redirect to={`/projects/${projectId}/entities/${goToEntity.id}`} />
        }

        return <Fragment>
            <Input startAdornment={<InputAdornment position="start"><SearchIcon /></InputAdornment>} onChange={this.onFilter} />
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
                progressComponent={<CircularProgress />}
            />
        </Fragment>
    }

    async fetchEntities() {

        let { projectId, contextId } = this.props

        let { page, size, sortColumn, sortDirection, filter } = this.state

        await this.setState(prevState => ({ ...prevState, loading: true }))

        let res = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${projectId}/entities?${
            new URLSearchParams({
                context: contextId,
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


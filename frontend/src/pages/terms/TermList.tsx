
import { Box, Button, CircularProgress, createStyles, darken, Grid, Input, InputAdornment, lighten, makeStyles, Paper, Tab, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Tabs, TextField, Theme, WithStyles, withStyles } from "@material-ui/core";
import SearchIcon from '@material-ui/icons/Search';
import React, { ChangeEvent, Fragment } from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import { Link, Redirect } from 'react-router-dom'
import DataTable from "react-data-table-component";
import Paginated from "../../dto/Paginated";
import MappingStatusBox from "../../components/MappingStatusBox";
import Spinner from "../../components/Spinner";
import Context from "../../dto/Context";
import Project from "../../dto/Project";
import ContextSelector from "../../components/ContextSelector";
import TermListing from "../../dto/TermListing";
import TermStatusBox from "../../components/TermStatusBox";
import { OntologyTermStatus } from "../../dto/OntologyTerm";
import { CloudDownload, DoneAll } from "@material-ui/icons";
import { post } from "../../api";
import ActionDialog from "./ActionDialog";
import FileSaver from 'file-saver';

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
    terms:Paginated<TermListing>|null
    goToTermListing:TermListing|null
    context:Context
    tab:string
    showActionDialog:boolean
}

class TermListingList extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            page: 0,
            size: 20,
            sortColumn: '',
            sortDirection: 'asc',
            filter: '',
            loading:true,
            terms: null,
            goToTermListing: null,
            context: props.project.contexts.filter(c => c.name === 'DEFAULT')[0]!,
            tab: 'NEEDS_IMPORT',
            showActionDialog: false
        }

        console.dir(this.state)


    }

    componentDidMount() {
        this.fetchTermListings()
    }

    // componentDidUpdate(prevProps:Props){
    //        this.props.projectId !== prevProps.projectId) {
    //                 this.fetchTermListings()
    //     }
    // }

    render() {

        let { classes, project } = this.props
        let { terms, goToTermListing, context, showActionDialog } = this.state

    let columns: any[] = [
        {
            name: 'Label',
            selector: 'label',
            sortable: true,
            cell: (t:TermListing) => t.ontologyTerm.label
        },
        {
            name: 'Status',
            selector: 'status',
            sortable: true,
            cell: (t:TermListing) => <TermStatusBox status={t.ontologyTerm.status} />
        },
        {
            name: 'Entity',
            selector: 'entity',
            sortable: true,
            cell: (t:TermListing) => t.entities.map(e => <Link to={`/projects/${project.id}/entities/${e.id}`}>{e.name}</Link>)
        },
    ]

        // if(terms === null) {
        //     return <CircularProgress />
        // }

        if(goToTermListing !== null) {
            // return <Redirect to={`/projects/${project.id}/terms/${goToTermListing.id}`} />
        }

        let actionable = this.state.tab === 'NEEDS_IMPORT'

        return <Fragment>

            <ActionDialog open={showActionDialog} onSubmit={this.actionTerms} onCancel={this.cancelAction} />

            <Grid container justify="space-between">
                <Grid item>
                    <h2>Terms</h2>
                </Grid>
                <Grid item>
                    <Box
      height="100%"
      display="flex"
      justifyContent="center"
      flexDirection="row"
    >
                    <Button variant="outlined" color="primary" onClick={this.downloadCSV} disabled={!actionable}><CloudDownload/> &nbsp; Download CSV</Button>
                    &nbsp;
                    <Button variant="outlined" color="primary" onClick={this.markActioned} disabled={!actionable}><DoneAll /> &nbsp; Mark All As Actioned</Button>
                    </Box>
                </Grid>
            </Grid>


             <Tabs
                indicatorColor="primary"
                textColor="primary"
                value={this.state.tab}
                onChange={this.changeTab}
            >
                <Tab label={"Deleted"} value="DELETED" />
                <Tab label={"Obsolete"} value="OBSOLETE" />
                <Tab label={"Needs Import"} value="NEEDS_IMPORT" />
                <Tab label={"Current"} value="CURRENT" />
            </Tabs>


            <Grid container>
                <Grid item xs={6}>
            <Input startAdornment={<InputAdornment position="start"><SearchIcon /></InputAdornment>} onChange={this.onFilter} />
            </Grid>
                <Grid item xs={6} container justify="flex-end">
                <ContextSelector project={project} context={context} onSwitchContext={this.onSwitchContext}  />
            </Grid>
            </Grid>
            <DataTable
                columns={columns}
                data={terms?.content || []}
                pagination
                paginationServer
                paginationTotalRows={terms?.totalElements || 0}
                paginationPerPage={this.state.size}
                paginationDefaultPage={this.state.page + 1}
                sortServer
                onSort={this.onSort}
                onChangeRowsPerPage={this.onChangeRowsPerPage}
                onChangePage={this.onChangePage}
                // onRowClicked={this.onClickTermListing}
                noHeader
                highlightOnHover
                pointerOnHover
                progressPending={this.state.loading}
                progressComponent={<Spinner />}
            />
        </Fragment>
    }

    async fetchTermListings() {

        let { project } = this.props
        let { context } = this.state

        let { page, size, sortColumn, sortDirection, filter } = this.state

        await this.setState(prevState => ({ ...prevState, loading: true }))

        let res = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${project.id}/ontology-terms?${
            new URLSearchParams({
                context: context!.name!,
                page: page.toString(),
                size: size.toString(),
                status: this.state.tab,
                ...(sortColumn ? { sort: sortColumn + ',' + sortDirection } : {}),
                search: filter
            })
        }`, {
            headers: { ...getAuthHeaders() }
        })

        let terms:Paginated<TermListing> = await res.json()

        this.setState(prevState => ({ ...prevState, terms, loading: false }))
    }

    // onClickTermListing = async (term:TermListing) => {
    //     this.setState(prevState => ({ ...prevState, goToTermListing: term }))
    // }

    onChangePage = async (page:number) => {
        await this.setState(prevState => ({ ...prevState, page: page-1 }))
        this.fetchTermListings()
    }

    onChangeRowsPerPage = async (rowsPerPage:number) => {
        await this.setState(prevState => ({ ...prevState, size: rowsPerPage }))
        this.fetchTermListings()
    }
    
    onSort = async (column: any, sortDirection:'asc'|'desc') => {
        await this.setState(prevState => ({ ...prevState, sortColumn: column.selector, sortDirection }))
        this.fetchTermListings()
    }

    onFilter = async (e:ChangeEvent<HTMLInputElement>) => {
        await this.setState(prevState => ({ ...prevState, filter: e.target.value }))
        this.fetchTermListings()
    }

    // onCreateTermListing = async (term:TermListing) => {

    //     let res = await fetch(process.env.REACT_APP_APIURL + '/v1/terms', {
    //         method: 'POST',
    //         headers: { ...getAuthHeaders(), 'content-type': 'application/json' },
    //         body: JSON.stringify(term)
    //     })

    //     if(res.status === 201) {
    //         this.fetchTermListings()
    //     } else {
    //         console.log('Error creating terms: ' + res.statusText)
    //     }
    // }

    onSwitchContext = async (context:Context) => {

        await this.setState(prevState => ({ ...prevState, context }))
        this.fetchTermListings()
    }

    changeTab = async (e:any, tab:string) => {
        await this.setState(prevState => ({ ...prevState, tab }))
        this.fetchTermListings()

    }

    downloadCSV = async () => {

        let { project } = this.props
        let { tab, context } = this.state

        let res = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${project.id}/ontology-terms/export`, {
            method: 'POST',
            body: JSON.stringify({
                status: tab,
                context: context.name
            }),
            headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
        })

        let b = await res.arrayBuffer()

        var blob = new Blob([b], { type: 'text/csv' })
        FileSaver.saveAs(blob, `${project.name}_${context.name}.csv`)
    }

    markActioned = async () => {
        await this.setState(prevState => ({ ...prevState, showActionDialog: true }))
    }

    actionTerms = async (comment:string) => {

        let { project } = this.props
        let { tab, context } = this.state

        let res = await post<any>(`/v1/projects/${project.id}/ontology-terms/action`, {
            status: tab,
            context: context.name,
            comment
        })

        this.fetchTermListings()
    }

    cancelAction = async () => {
        await this.setState(prevState => ({ ...prevState, showActionDialog: false }))
    }
}

export default withStyles(styles)(TermListingList)



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
    context:Context|null,
    tab:string
    showActionDialog:boolean
    stats:any
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
            context: null,
            tab: 'NEEDS_IMPORT',
            showActionDialog: false,
            stats:null
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
        let { terms, goToTermListing, context, showActionDialog, stats } = this.state

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
            name: 'Entities',
            selector: 'entity',
            sortable: true,
            cell: (t:TermListing) =>  <Box> {t.entities.map(e => <Fragment>
		    <Link to={`/projects/${project.id}/entities/${e.id}`}>{e.name}</Link>
		    <br/>
	</Fragment>
	)}
		    </Box>
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
                        <Button variant="outlined" color="primary" onClick={this.downloadCSV} disabled={!actionable}><CloudDownload /> &nbsp; Download CSV</Button>
                        &nbsp;
                        <Button variant="outlined" color="primary" onClick={this.markActioned} disabled={!actionable}><DoneAll /> &nbsp; Mark All As Actioned</Button>
                    </Box>
                </Grid>
            </Grid>


            <p>
                This list displays all of the ontology terms to which entities have been mapped in this project. Where the term status is <b>Needs Import</b>, it does not exist in any of the preferred ontologies, and therefore needs to be added.
            </p>
            <p>
                OntoString allows all of the terms marked as <b>Needs Import</b> to be downloaded as a CSV file. This file can be used to inform the manual process of requesting that the terms are added to the correct ontology.
            </p>
            <p>
                When the terms no longer require attention (i.e., issues have been opened with the relevant ontology issue tracker), they can be marked as <b>actioned</b>, at which point they will no longer be displayed in the <b>Needs Import</b> list.
            </p>
            <p>
                Finally, OntoString will pick up the terms when they are added to the ontology, and display them in the <b>Current</b> list, which is the end of the curation workflow.
            </p>

		<Grid container>
			<Grid item xs={6}>
				<Input startAdornment={<InputAdornment position="start"><SearchIcon /></InputAdornment>} onChange={this.onFilter} />
			</Grid>
			<Grid item xs={6} container justify="flex-end">
				<ContextSelector project={project} context={context} onSwitchContext={this.onSwitchContext} />
			</Grid>
            </Grid>

	
	    {context &&
	    <Fragment>

             <Tabs
                indicatorColor="primary"
                textColor="primary"
                value={this.state.tab}
                onChange={this.changeTab}
            >
                <Tab label={`Deleted (${stats?.DELETED || '0'})`} value='DELETED' />
                <Tab label={`Obsolete (${stats?.OBSOLETE || '0'})`} value='OBSOLETE' />
                <Tab label={`Needs Import (${stats?.NEEDS_IMPORT || '0'})`} value='NEEDS_IMPORT' />
                {/* <Tab label={`Current (${stats?.CURRENT || '0'})`} value='CURRENT' /> */}
                <Tab label={`Current`} value='CURRENT' />
            </Tabs>


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


	    </Fragment>}


        </Fragment>
    }

    async fetchTermListings() {

        let { project } = this.props
        let { context } = this.state

        let { page, size, sortColumn, sortDirection, filter } = this.state



	/// TODO: remove when backend fixed
	if(!context)
		return


        await this.setState(prevState => ({ ...prevState, loading: true }))

        let [res,statsRes] = await Promise.all([
            fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${project.id}/ontology-terms?${
                new URLSearchParams({
		    ...(context ? { context: context.name! } : {}),
                    page: page.toString(),
                    size: size.toString(),
                    status: this.state.tab,
                    ...(sortColumn ? { sort: sortColumn + ',' + sortDirection } : {}),
                    search: filter
                })
            }`, {
                headers: { ...getAuthHeaders() }
            }),
            fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${project.id}/ontology-terms-stats?${
                new URLSearchParams({
		    ...(context ? { context: context.name! } : {}),
                })
            }`, {
                headers: { ...getAuthHeaders() }
            })
        ])

        let terms:Paginated<TermListing> = await res.json()
        let stats:any = await statsRes.json()

        this.setState(prevState => ({ ...prevState, terms, loading: false, stats }))
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

    onSwitchContext = async (context:Context|null) => {

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
		    ...(context ? { context: context.name! } : {}),
            }),
            headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
        })

        let b = await res.arrayBuffer()

        var blob = new Blob([b], { type: 'text/csv' })

	let filename = context ? 
		`${project.name}_${context.name}.csv` :
		`${project.name}.csv`

        FileSaver.saveAs(blob, filename)
    }

    markActioned = async () => {
        await this.setState(prevState => ({ ...prevState, showActionDialog: true }))
    }

    actionTerms = async (comment:string) => {

        let { project } = this.props
        let { tab, context } = this.state

        await this.setState(prevState => ({ ...prevState, loading: true, showActionDialog: false }))

        let res = await fetch(`${process.env.REACT_APP_APIURL}/v1/projects/${project.id}/ontology-terms/action`, {
            method: 'POST',
            body: JSON.stringify({
                status: tab,
		    ...(context ? { context: context.name! } : {}),
                comment
            }),
            headers: { ...getAuthHeaders(), 'Content-Type': 'application/json' }
        })

        this.fetchTermListings()
    }

    cancelAction = async () => {
        await this.setState(prevState => ({ ...prevState, showActionDialog: false }))
    }
}

export default withStyles(styles)(TermListingList)


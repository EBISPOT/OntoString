

import React from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import CircularProgress from '@material-ui/core/CircularProgress';
import { get } from '../../api';
import { Grid } from '@material-ui/core';
import { OlsSearchResults, OlsSearchResult } from '../../dto/OlsSearchResults';

interface Props {
    projectId:string
    contextName:string
    onSelectTerm:(term:OlsSearchResult)=>void
    mode:string
}

interface State {
    open:boolean
    options:OlsSearchResult[]
    query:string
    loading:boolean
}

export default class OntologyTermSearchBox extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
            open: false,
            options: [],
            query: '',
            loading: false
        }

    }

    componentDidMount() {
        this.load();
    }

    async load() {

        let { projectId, contextName, mode } = this.props
        let { query } = this.state

        this.setState(prevState => ({ ...prevState, loading: true }))

        const res: OlsSearchResults = await get(`/v1/projects/${projectId}/searchOLS?${new URLSearchParams({
            query,
            context: contextName
        })}`)

        this.setState(prevState => ({ ...prevState, loading: false, options: res.results[this.props.mode] || [] }))

    }

    render() {

        let { open, options, query, loading } = this.state

        console.log('rendering with options')
        console.dir(options)

        return (
            <Autocomplete
            id="asynchronous-demo"
            style={{ width: 500 }}
            open={open}
            onOpen={this.onOpen}
            onClose={this.onClose}
            onChange={this.onAutocompleteChange as any}
            // getOptionSelected={(option:OlsSearchResult, value:OlsSearchResult) => option.iri === value.iri}
            getOptionLabel={(option:OlsSearchResult) => option.iri}
            renderOption={(option:OlsSearchResult) =>
<Grid
  container
  direction="row"
  justify="space-between"
  alignItems="center"
>
                
                <span>{option.label || option.obo_id}</span><span style={{
                backgroundColor: '#999',
                padding: '0 10px',
                lineHeight: '1.5',
                fontSize: '.875rem',
                color: '#fff',
                verticalAlign: 'middle',
                whiteSpace: 'nowrap',
                textAlign: 'center',
                borderRadius: '0.6rem',
                textTransform: 'uppercase',
            }}>{option.ontology_name}</span>
            
            </Grid>
        
        }
            filterOptions={x => x}
            options={options}
            loading={loading}
            renderInput={(params) => (
                <TextField
                {...params}
                label="Search..."
                variant="outlined"
                value={query}
                onChange={this.onChange}
                InputProps={{
                    ...params.InputProps,
                    endAdornment: (
                    <React.Fragment>
                        {loading ? <CircularProgress color="inherit" size={20} /> : null}
                        {params.InputProps.endAdornment}
                    </React.Fragment>
                    ),
                }}
                />
            )}
            />
        )

    }

    onOpen = () => {
        this.setState(prevState => ({ ...prevState, open: true }))
    }

    onClose = () => {
        this.setState(prevState => ({ ...prevState, open: false }))
    }

    onChange = async (e: any) => {
        await this.setState(prevState => ({ ...prevState, query: e.target.value }))
        this.load()
    }

    onAutocompleteChange = async (e: any, option:OlsSearchResult) => {
        this.props.onSelectTerm(option)
    }
}
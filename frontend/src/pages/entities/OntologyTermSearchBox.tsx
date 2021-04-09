

import React from 'react';
import TextField from '@material-ui/core/TextField';
import Autocomplete from '@material-ui/lab/Autocomplete';
import CircularProgress from '@material-ui/core/CircularProgress';
import { get } from '../../api';
import OlsResponse, { OlsSearchResult } from '../../dto/OlsSearchResult';

export default function OntologyTermSearchBox(projectId:string) {
  const [open, setOpen] = React.useState<boolean>(false);
  const [options, setOptions] = React.useState<OlsSearchResult[]>([]);
  const loading = open && options.length === 0;

  React.useEffect(() => {
    let active = true;

    if (!loading) {
      return undefined;
    }

    (async () => {

      const res:OlsResponse = await get(`/v1/projects/${projectId}/searchOLS?${
        new URLSearchParams({
            query: ''
        })}`)

      console.dir(res)

      if (active) {
        setOptions(res.docs)
      }
    })();

    return () => {
      active = false;
    };
  }, [loading]);

  React.useEffect(() => {
    if (!open) {
      setOptions([]);
    }
  }, [open]);

  return (
    <Autocomplete
      id="asynchronous-demo"
      style={{ width: 300 }}
      open={open}
      onOpen={() => {
        setOpen(true);
      }}
      onClose={() => {
        setOpen(false);
      }}
      getOptionSelected={(option:OlsSearchResult, value:OlsSearchResult) => option.iri === value.iri}
      getOptionLabel={(option:OlsSearchResult) => option.iri}
      options={options}
      loading={loading}
      renderInput={(params) => (
        <TextField
          {...params}
          label="Asynchronous"
          variant="outlined"
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
  );
}
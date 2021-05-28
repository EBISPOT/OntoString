import { Button, FormControl, Paper, Tab, Tabs, TextField } from "@material-ui/core";
import { Autocomplete } from "@material-ui/lab";
import React, { Fragment } from "react";
import { post } from "../api";
import Context from "../dto/Context";
import Project from "../dto/Project";


export interface Props {
    project:Project
    context:Context
    onSwitchContext: (context:Context) => void
}

export interface State {
    contexts:Context[]
    showCreateContextDialog:boolean
}

export default class ContextSelector extends React.Component<Props, State> {
    
    constructor (props:Props) {
        super(props)

        this.state = {
            showCreateContextDialog: false,
            contexts: props.project.contexts
        }
    }

    render() {

        let { project, context, onSwitchContext } = this.props
        
        let { showCreateContextDialog } = this.state

        // return <Fragment>
        //     { showCreateContextDialog && <CreateContextDialog onCreate={this.createContext} onClose={this.closeCreateContext} /> }
        //     <Tabs
        //         indicatorColor="primary"
        //         textColor="primary"
        //         value={context.name}
        //     // onChange={handleChange}
        //     >
        //         {project.contexts.map(c => <Tab value={c.name} label={c.name} onClick={() => onSwitchContext(c)} />)}
        //         <Tab label={"+ New Context"} onClick={this.newContext} />
        //     </Tabs>
        // </Fragment>

        // const CreateComponent = ({ children, ...other }) => (
        //     <Paper {...other}>
        //       {children}
        //       <span style={{display: 'block',padding:'8px'}}>
        //             <Button variant="outlined" color="primary" onMouseDown={this.newContext}>
        //                 + Create Context</Button>
        //         </span>
        //     </Paper>
        //   );


        return <Fragment>

            {/* {showCreateContextDialog && <CreateContextDialog onCreate={this.createContext} onClose={this.closeCreateContext} />} */}
            
            <Autocomplete
            value={context}
            onChange={(event:any, newValue:Context|null) => {
                console.log('onchange' + newValue)
                console.dir(newValue)
                if(newValue)
                    this.setContext(newValue);
            }}
            id="controllable-states-demo"
            options={this.state.contexts}
            style={{ width: 300 }}
            getOptionLabel={(option:Context) => option.name}
            renderInput={(params) => <TextField {...params} />}
            // PaperComponent={CreateComponent as any}
            disableClearable
        />

        </Fragment>

    }

    newContext = (e) => {
        this.setState(prevState => ({ ...prevState, showCreateContextDialog: true }))
    }

    // createContext = async (context:Context) => {

    //     let { project } = this.props

    //     await post<Context>(`/v1/projects/${project.id}/contexts`, context)

    //     await this.setState(prevState => ({ ...prevState, contexts: [...prevState.contexts, context] }))

    //     this.props.onSwitchContext(context)

    //     this.setState(prevState => ({ ...prevState, showCreateContextDialog: false }))
    // }

    // closeCreateContext = () => {
    //     this.setState(prevState => ({ ...prevState, showCreateContextDialog: false }))
    // }

    setContext = (context:Context) => {
        this.props.onSwitchContext(context)
    }


}




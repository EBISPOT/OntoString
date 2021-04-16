import React, { Fragment, useState } from 'react';
import { BrowserRouter, Link, Route, RouteComponentProps, Switch, withRouter } from "react-router-dom";
import logo from './logo.svg';
import './App.css';
import { AppBar, createStyles, Tab, Tabs, Theme, WithStyles, withStyles } from '@material-ui/core';

import Home from './pages/Home'
import Login from './pages/Login'
import ProjectPage from './pages/projects/ProjectPage'
import { AuthProvider } from './auth-context';
import EntityPage from './pages/entities/EntityPage';

let styles = (theme:Theme) => createStyles({
    main: {
      padding: theme.spacing(3),
      [theme.breakpoints.down('xs')]: {
        padding: theme.spacing(2),
      },
    },
  });

interface AppProps extends WithStyles<typeof styles> {
}
  
function App(props:AppProps) {

  let { classes } = props

  return (
      <Fragment>
          <BrowserRouter basename={process.env.PUBLIC_URL}>

<header style={{padding:'16px',backgroundColor:'black',backgroundImage:'url(\'' + process.env.PUBLIC_URL + '/embl-ebi-background-4.jpg\')',backgroundPosition:'100% 100%'}}>
                <img style={{height:'100px'}} src={process.env.PUBLIC_URL + "/curator.svg"} />


                <nav>
                        <ul className="dropdown menu float-left" data-description="navigational" role="menubar" data-dropdown-menu="6mg2ht-dropdown-menu">
                            <li  role="menuitem"><Link to="/ols/index">Projects</Link></li>
                            <li  role="menuitem"><Link to="/ols/ontologies">Entities</Link></li>
                            <li  role="menuitem"><Link to="/ols/docs">Terms</Link></li>
                            <li  role="menuitem"><Link to="/ols/about">About</Link></li>
                        </ul>
                    </nav>



                </header>

              <main className={classes.main}>
                <Switch>
                    <Route exact path={`/`} component={Home} />

                    <Route exact path={`/login`} component={Login}></Route>

                    <Route exact path={`/projects/:id`}
                        component={(props:any) => <ProjectPage id={props.match.params.id}/>}></Route>

                    <Route exact path={`/projects/:projectId/entities/:entityId`}
                        component={(props:any) => <EntityPage projectId={props.match.params.projectId} entityId={props.match.params.entityId} />}></Route>
                </Switch>
              </main>
          </BrowserRouter>
      </Fragment>

  );

}

export default withStyles(styles)(App)



import React, { Fragment, useState } from 'react';
import { BrowserRouter, Link, Redirect, Route, RouteComponentProps, Switch, withRouter } from "react-router-dom";
import logo from './logo.svg';
import './App.css';
import { AppBar, createStyles, Tab, Tabs, Theme, WithStyles, withStyles } from '@material-ui/core';

import Home from './pages/Home'
import Login from './pages/Login'
import { AuthProvider } from './auth-context';
import EntityPage from './pages/entities/EntityPage';
import ProjectsPage from './pages/projects/ProjectsPage';
import EntitiesPage from './pages/entities/EntitiesPage';
import TermsPage from './pages/terms/TermsPage';
import About from './pages/About';

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
          <BrowserRouter basename={process.env.PUBLIC_URL}>

                <Switch>
                    <Route exact path={`/`} component={Home} />

                    <Route exact path={`/login`} component={Login}></Route>

                    <Route exact path={`/projects`} component={ProjectsPage} />

                    <Route exact path={`/projects/:id`}
                        component={(props:any) => <Redirect to={`/projects/${props.match.params.id}/entities`}/>}></Route>

                    <Route exact path={`/projects/:id/entities`}
                        component={(props:any) => <EntitiesPage projectId={props.match.params.id}/>}></Route>

                    <Route exact path={`/projects/:id/terms`}
                        component={(props:any) => <TermsPage projectId={props.match.params.id}/>}></Route>

                    <Route exact path={`/projects/:projectId/entities/:entityId`}
                        component={(props:any) => <EntityPage projectId={props.match.params.projectId} entityId={props.match.params.entityId} />}></Route>

                    <Route exact path={`/about`}
                        component={(props:any) => <About projectId={ new URLSearchParams(props.location.search).get('projectId') || undefined} />}></Route>
                </Switch>
          </BrowserRouter>

  );

}

export default withStyles(styles)(App)



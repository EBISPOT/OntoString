import React, { Fragment, useState } from 'react';
import { BrowserRouter, Route, RouteComponentProps, Switch, withRouter } from "react-router-dom";
import logo from './logo.svg';
import './App.css';
import { createStyles, Theme, WithStyles, withStyles } from '@material-ui/core';

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



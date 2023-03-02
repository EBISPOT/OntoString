import { createStyles, Theme, WithStyles, withStyles } from "@material-ui/core";
import React from "react";
import { BrowserRouter, Redirect, Route, Switch } from "react-router-dom";

import { get } from "./api";
import { isLoggedIn } from "./auth";
import Footer from "./components/Footer";
import User from "./dto/User";
import About from "./pages/About";
import Admin from "./pages/admin/AdminPage";
import EntitiesPage from "./pages/entities/EntitiesPage";
import EntityPage from "./pages/entities/EntityPage";
import Help from "./pages/Help";
import Home from "./pages/Home";
import Login from "./pages/Login";
import ContextSettingsPage from "./pages/projects/ContextSettingsPage";
import ProjectSettingsPage from "./pages/projects/ProjectSettingsPage";
import ProjectsPage from "./pages/projects/ProjectsPage";
import TermsPage from "./pages/terms/TermsPage";
import UserContext from "./UserContext";

let styles = (theme: Theme) =>
  createStyles({
    main: {
      padding: theme.spacing(3),
      [theme.breakpoints.down("xs")]: {
        padding: theme.spacing(2),
      },
    },
  });

interface Props extends WithStyles<typeof styles> {}

interface State {
  userLoaded: boolean;
  user: User | null;
}

class App extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      userLoaded: false,
      user: null,
    };
  }

  componentDidMount() {
    this.setState((prevState) => ({ ...prevState, userLoaded: true }));
    this.fetchUser();
  }

  async fetchUser() {
    if (!isLoggedIn()) {
      return;
    }

    let user = await get<User>(`/v1/me`);

    this.setState((prevState) => ({ ...prevState, user }));
  }

  render() {
    let { classes } = this.props;

    return (
      <BrowserRouter basename={process.env.PUBLIC_URL}>
        <UserContext.Provider value={this.state.user}>
          <Switch>
            <Route exact path={`/`} component={Home} />

            <Route exact path={`/login`} component={Login}></Route>

            <Route exact path={`/projects`} component={ProjectsPage} />

            <Route
              exact
              path={`/projects/:id`}
              component={(props: any) => (
                <Redirect to={`/projects/${props.match.params.id}/entities`} />
              )}
            ></Route>

            <Route
              exact
              path={`/projects/:id/settings`}
              component={(props: any) => (
                <ProjectSettingsPage projectId={props.match.params.id} />
              )}
            ></Route>

            <Route
              exact
              path={`/projects/:projectId/contexts/:contextName`}
              component={(props: any) => (
                <ContextSettingsPage
                  projectId={props.match.params.projectId}
                  contextName={props.match.params.contextName}
                />
              )}
            ></Route>

            <Route
              exact
              path={`/projects/:id/entities`}
              component={(props: any) => (
                <EntitiesPage projectId={props.match.params.id} />
              )}
            ></Route>

            <Route
              exact
              path={`/projects/:id/terms`}
              component={(props: any) => (
                <TermsPage projectId={props.match.params.id} />
              )}
            ></Route>

            <Route
              exact
              path={`/projects/:projectId/entities/:entityId`}
              component={(props: any) => (
                <EntityPage
                  projectId={props.match.params.projectId}
                  entityId={props.match.params.entityId}
                />
              )}
            ></Route>

            <Route
              exact
              path={`/help`}
              component={(props: any) => (
                <Help
                  projectId={
                    new URLSearchParams(props.location.search).get(
                      "projectId"
                    ) || undefined
                  }
                />
              )}
            ></Route>

            <Route
              exact
              path={`/about`}
              component={(props: any) => (
                <About
                  projectId={
                    new URLSearchParams(props.location.search).get(
                      "projectId"
                    ) || undefined
                  }
                />
              )}
            ></Route>

            <Route
              exact
              path={`/admin`}
              component={(props: any) => (
                <Admin
                  projectId={
                    new URLSearchParams(props.location.search).get(
                      "projectId"
                    ) || undefined
                  }
                />
              )}
            ></Route>
          </Switch>
        </UserContext.Provider>
        <Footer />
      </BrowserRouter>
    );
  }
}

export default withStyles(styles)(App);

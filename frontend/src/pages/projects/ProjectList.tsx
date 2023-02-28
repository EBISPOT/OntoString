import {
    createStyles,
    IconButton,
    lighten,
    Theme,
    WithStyles,
    withStyles
} from "@material-ui/core";
import { Settings } from "@material-ui/icons";
import React from "react";
import DataTable from "react-data-table-component";
import { Redirect } from "react-router-dom";
import { getAuthHeaders } from "../../auth";
import Spinner from "../../components/Spinner";
import Project from "../../dto/Project";
import formatDate from "../../formatDate";
import CreateProjectDialog from "./CreateProjectDialog";

const styles = (theme: Theme) =>
  createStyles({
    tableRow: {
      "&": {
        cursor: "pointer",
      },
      "&:hover": {
        backgroundColor: lighten(theme.palette.primary.light, 0.85),
      },
    },
  });

interface Props extends WithStyles<typeof styles> {}

interface State {
  projects: Project[] | null;
  goToProject: Project | null;
  goToProjectSettings: Project | null;
}

class ProjectList extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      projects: null,
      goToProject: null,
      goToProjectSettings: null,
    };
  }

  componentDidMount() {
    this.fetchProjects();
  }

  columns: any[] = [
    {
      name: "Name",
      selector: "name",
      sortable: true,
    },
    {
      name: "Description",
      selector: "description",
      sortable: true,
    },
    {
      name: "Created by",
      selector: "created.user.name",
      sortable: true,
    },
    {
      name: "Created",
      sortable: true,
      sortFunction: (a: Project, b: Project) => {
        const keyA = a.created.timestamp;
        const keyB = b.created.timestamp;
        return keyA === keyB ? 0 : keyA > keyB ? 1 : -1;
      },
      selector: (project: Project) => {
        return formatDate(project.created!.timestamp);
      },
    },
    {
      name: "",
      ignoreRowClick: true,
      center: true,
      cell: (project: Project) => {
        return (
          <IconButton
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              this.onClickProjectSettings(project);
            }}
          >
            <Settings />
          </IconButton>
        );
      },
    },
  ];

  render() {
    let { projects, goToProject, goToProjectSettings } = this.state;
    let { classes } = this.props;

    if (projects === null) {
      return <Spinner />;
    }

    if (goToProject !== null) {
      return <Redirect to={`/projects/${goToProject.id}`} />;
    }

    if (goToProjectSettings !== null) {
      return <Redirect to={`/projects/${goToProjectSettings.id}/settings`} />;
    }

    return (
      <div>
        <div style={{ textAlign: "right" }}>
          <CreateProjectDialog onCreate={this.onCreateProject} />
        </div>
        <DataTable
          columns={this.columns}
          data={projects || []}
          pagination
          paginationTotalRows={projects.length}
          paginationPerPage={10}
          paginationDefaultPage={1}
          onRowClicked={(project: Project) => {
            this.onClickProject(project);
          }}
          paginationRowsPerPageOptions={[10, 25, 100]}
          noHeader
          highlightOnHover
          pointerOnHover
          progressPending={projects === null}
          progressComponent={<Spinner />}
        />
      </div>
    );
  }

  async fetchProjects() {
    await this.setState((prevState) => ({ ...prevState, projects: null }));

    let res = await fetch(process.env.REACT_APP_APIURL + "/v1/projects", {
      headers: { ...getAuthHeaders() },
    });

    let projects = await res.json();

    this.setState((prevState) => ({ ...prevState, projects }));
  }

  onClickProject = async (project: Project) => {
    this.setState((prevState) => ({ ...prevState, goToProject: project }));
  };

  onClickProjectSettings = async (project: Project) => {
    this.setState((prevState) => ({
      ...prevState,
      goToProjectSettings: project,
    }));
  };

  onCreateProject = async (project: Project) => {
    let res = await fetch(process.env.REACT_APP_APIURL + "/v1/projects", {
      method: "POST",
      headers: { ...getAuthHeaders(), "content-type": "application/json" },
      body: JSON.stringify(project),
    });

    if (res.status === 201) {
      this.fetchProjects();
    } else {
      console.log("Error creating projects: " + res.statusText);
    }
  };
}

export default withStyles(styles)(ProjectList);

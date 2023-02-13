import {
  createStyles,
  IconButton,
  Theme,
  WithStyles,
  withStyles
} from "@material-ui/core";
import { Settings } from "@material-ui/icons";
import React from "react";
import DataTable from "react-data-table-component";
import { Redirect } from "react-router-dom";
import { post } from "../../api";
import Spinner from "../../components/Spinner";
import Context from "../../dto/Context";
import Project from "../../dto/Project";
import CreateContextDialog from "./CreateContextDialog";

const styles = (theme: Theme) =>
  createStyles({
    tableRow: {
      // "&": {
      //     cursor: 'pointer'
      // },
      // "&:hover": {
      //     backgroundColor: lighten(theme.palette.primary.light, 0.85)
      // }
    },
  });

interface Props extends WithStyles<typeof styles> {
  project: Project;
  onCreateContext: () => void;
}

interface State {
  goToContext: Context | null;
}

class ContextList extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      goToContext: null,
    };
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
      name: "",
      center: true,
      cell: (context: Context) => {
        return (
          <IconButton
            onClick={(e) => {
              e.preventDefault();
              e.stopPropagation();
              this.onClickContextSettings(context);
            }}
          >
            <Settings />
          </IconButton>
        );
      },
    },
  ];

  render() {
    let { goToContext } = this.state;
    let { project } = this.props;

    if (goToContext !== null) {
      return (
        <Redirect to={`/projects/${project.id}/contexts/${goToContext.name}`} />
      );
    }

    return (
      <div>
        <div style={{ textAlign: "right" }}>
          <CreateContextDialog onCreate={this.createContext} />
        </div>
        <DataTable
          columns={this.columns}
          data={project.contexts || []}
          pagination
          paginationTotalRows={project.contexts.length}
          paginationPerPage={10}
          paginationDefaultPage={1}
          paginationRowsPerPageOptions={[10, 25, 100]}
          noHeader
          highlightOnHover
          progressPending={project.contexts === null}
          progressComponent={<Spinner />}
        />
      </div>
    );
  }

  onClickContextSettings = async (context: Context) => {
    this.setState((prevState) => ({ ...prevState, goToContext: context }));
  };

  createContext = async (context: Context) => {
    let { project } = this.props;

    await post<Context>(`/v1/projects/${project.id}/contexts`, context);

    this.props.onCreateContext();

    this.setState((prevState) => ({
      ...prevState,
      showCreateContextDialog: false,
    }));
  };
}

export default withStyles(styles)(ContextList);

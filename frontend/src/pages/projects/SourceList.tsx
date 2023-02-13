import { createStyles, Theme, WithStyles, withStyles } from "@material-ui/core";
import React from "react";
import DataTable from "react-data-table-component";
import { Redirect } from "react-router-dom";
import { get, post } from "../../api";
import Spinner from "../../components/Spinner";
import Project from "../../dto/Project";
import Source from "../../dto/Source";
import CreateSourceDialog from "./CreateSourceDialog";

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
  onCreateSource: () => void;
}

interface State {
  loading: boolean;
  sources: Source[] | null;
  goToSource: Source | null;
}

class SourceList extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      sources: [],
      loading: true,
      goToSource: null,
    };
  }

  componentDidMount() {
    this.fetch();
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
      name: "Type",
      selector: "type",
      sortable: true,
    },
    {
      name: "URL",
      selector: "uri",
      sortable: true,
    },
  ];

  render() {
    let { goToSource, sources, loading } = this.state;
    let { project } = this.props;

    if (goToSource !== null) {
      return (
        <Redirect to={`/projects/${project.id}/sources/${goToSource.name}`} />
      );
    }

    if (loading || !sources) {
      return <Spinner />;
    }

    return (
      <div>
        <div style={{ textAlign: "right" }}>
          <CreateSourceDialog onCreate={this.createSource} />
        </div>
        <DataTable
          columns={this.columns}
          data={sources || []}
          pagination
          paginationTotalRows={sources.length}
          paginationPerPage={10}
          paginationDefaultPage={1}
          paginationRowsPerPageOptions={[10, 25, 100]}
          noHeader
          highlightOnHover
          progressPending={sources === null}
          progressComponent={<Spinner />}
        />
      </div>
    );
  }

  async fetch() {
    let { project } = this.props;

    await this.setState((prevState) => ({ ...prevState, loading: true }));

    let sources = await get<Source[]>(`/v1/projects/${project.id}/sources`);

    this.setState((prevState) => ({ ...prevState, sources, loading: false }));
  }

  onClickSourceSettings = async (source: Source) => {
    this.setState((prevState) => ({ ...prevState, goToSource: source }));
  };

  createSource = async (source: Source) => {
    let { project } = this.props;

    await post<Source>(`/v1/projects/${project.id}/sources`, source);
    await this.fetch();

    this.props.onCreateSource();

    this.setState((prevState) => ({
      ...prevState,
      showCreateSourceDialog: false,
    }));
  };
}

export default withStyles(styles)(SourceList);

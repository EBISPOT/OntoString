import {
  Button,
  createStyles,
  MenuItem,
  Select,
  Table,
  TableBody,
  TableCell,
  TableRow,
  Theme,
  WithStyles,
  withStyles
} from "@material-ui/core";
import { Delete } from "@material-ui/icons";
import React from "react";
import DataTable from "react-data-table-component";
import { Redirect } from "react-router-dom";
import { get, post, put } from "../../api";
import Spinner from "../../components/Spinner";
import Paginated from "../../dto/Paginated";
import Project from "../../dto/Project";
import User from "../../dto/User";
import AddProjectDialog from "./AddProjectDialog";
import CreateUserDialog from "./CreateUserDialog";

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
  onCreateUser: () => void;
}

interface State {
  loading: boolean;
  projects: Project[];
  users: Paginated<User> | null;
  goToUser: User | null;
}

class UserList extends React.Component<Props, State> {
  constructor(props: Props) {
    super(props);

    this.state = {
      projects: [],
      users: null,
      loading: true,
      goToUser: null,
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
      name: "Email",
      selector: "email",
      sortable: true,
    },
    {
      name: "Projects",
      cell: (user: User) => {
        return (
          <div>
            <Table size="small">
              <TableBody>
                {user.roles.map((role) => {
                  let project = this.state.projects.filter(
                    (p) => p.id === role.projectId
                  )[0];
                  return (
                    <TableRow>
                      <TableCell>{project.name}</TableCell>
                      <TableCell>
                        <Select
                          value={role.role}
                          onChange={(ev) => this.changeRole(user, project, ev)}
                        >
                          <MenuItem value="CURATOR">CURATOR</MenuItem>
                          <MenuItem value="CONTRIBUTOR">CONTRIBUTOR</MenuItem>
                          <MenuItem value="ADMIN">ADMIN</MenuItem>
                        </Select>
                      </TableCell>
                      <TableCell>
                        <Button>
                          <Delete />
                        </Button>
                      </TableCell>
                    </TableRow>
                  );
                })}
                <TableRow>
                  <TableCell colSpan={2}>
                    <AddProjectDialog
                      projects={this.state.projects}
                      onAdd={(project) => this.onAddProject(user, project)}
                    />
                  </TableCell>
                </TableRow>
              </TableBody>
            </Table>
          </div>
        );
      },
    },
  ];

  render() {
    let { goToUser, users, loading } = this.state;

    if (goToUser !== null) {
      return <Redirect to={`/users/${goToUser.name}`} />;
    }

    if (loading || !users) {
      return <Spinner />;
    }

    return (
      <div>
        <div style={{ textAlign: "right" }}>
          <CreateUserDialog onCreate={this.createUser} />
        </div>
        <DataTable
          columns={this.columns}
          data={users.content || []}
          pagination
          paginationTotalRows={users.content.length}
          paginationPerPage={10}
          paginationDefaultPage={1}
          paginationRowsPerPageOptions={[10, 25, 100]}
          noHeader
          highlightOnHover
          progressPending={users === null}
          progressComponent={<Spinner />}
        />
      </div>
    );
  }

  async fetch() {
    await this.setState((prevState) => ({ ...prevState, loading: true }));

    let [users, projects] = await Promise.all([
      get<Paginated<User>>(`/v1/users`),
      get<Project[]>(`/v1/projects`),
    ]);

    this.setState((prevState) => ({
      ...prevState,
      users,
      projects,
      loading: false,
    }));
  }

  onClickUserSettings = async (user: User) => {
    this.setState((prevState) => ({ ...prevState, goToUser: user }));
  };

  createUser = async (user: User) => {
    await post<User>(`/v1/users`, user);

    this.props.onCreateUser();

    this.setState((prevState) => ({
      ...prevState,
      showCreateUserDialog: false,
    }));

    this.fetch();
  };

  changeRole = async (user: User, project: Project, ev: any) => {
    for (let r of user.roles) {
      if (r.projectId === project.id) {
        r.role = ev.target.value;
        await put<any>(`/v1/projects/${project.id}/users/${user.id}`, {
          user,
          roles: [r.role],
        });
      }
    }

    this.props.onCreateUser();

    this.setState((prevState) => ({
      ...prevState,
      showCreateUserDialog: false,
    }));
  };

  onAddProject = async (user: User, project: Project) => {
    await put<any>(`/v1/projects/${project.id}/users/${user.id}`, {
      user,
      roles: ["ADMIN"],
    });
    this.fetch();
  };
}

export default withStyles(styles)(UserList);

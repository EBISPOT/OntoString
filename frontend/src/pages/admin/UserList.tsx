
import { Button, Checkbox, CircularProgress, createStyles, darken, IconButton, lighten, makeStyles, MenuItem, Paper, Select, Table, TableBody, TableCell, TableContainer, TableHead, TableRow, Theme, WithStyles, withStyles } from "@material-ui/core";
import React from "react";
import { useState, useEffect } from "react";
import { getAuthHeaders, getToken, isLoggedIn } from "../../auth";
import User from "../../dto/User";
import CreateUserDialog from "./CreateUserDialog";
import { Link, Redirect } from 'react-router-dom'
import formatDate from "../../formatDate";
import Spinner from "../../components/Spinner";
import { Settings } from "@material-ui/icons";
import Project from "../../dto/Project";
import Paginated from "../../dto/Paginated";
import { get, post, put } from "../../api";
import { Delete } from '@material-ui/icons'
import AddProjectDialog from "./AddProjectDialog";

const styles = (theme:Theme) => createStyles({
    tableRow: {
        // "&": {
        //     cursor: 'pointer'
        // },
        // "&:hover": {
        //     backgroundColor: lighten(theme.palette.primary.light, 0.85)
        // }
    }
})

interface Props extends WithStyles<typeof styles> {
    onCreateUser:()=>void
}

interface State {
    loading:boolean
    projects:Project[]
    users:Paginated<User>|null
    goToUser:User|null
}

class UserList extends React.Component<Props, State> {

    constructor(props:Props) {

        super(props)

        this.state = {
		projects: [],
            users: null,
            loading: true,
            goToUser: null
        }
    }

    componentDidMount() {
        this.fetch()
    }

    render() {

        let { goToUser, users, loading } = this.state
        let { classes } = this.props

        if(goToUser !== null) {
            return <Redirect to={`/users/${goToUser.name}`} />
        }

        if(loading || !users) {
            return <Spinner/>
        }

        return <TableContainer component={Paper}>
        <Table size="small" aria-label="simple table">
          <TableHead>
            <TableRow>
              <TableCell>Name</TableCell>
              <TableCell align="left">Email</TableCell>
              {/* <TableCell align="left">Created by</TableCell>
              <TableCell align="left">Created</TableCell> */}
              <TableCell align="left">Projects</TableCell>
            </TableRow>
          </TableHead>
          <TableBody>
            {users.content.map((user:User) => (
              <TableRow className={classes.tableRow} key={user.name}>
                <TableCell component="th" scope="row">
                    {user.name}
                </TableCell>
                <TableCell align="left">
                    {user.email}
                </TableCell>
                <TableCell align="left">
				<Table size="small">
					<TableBody>
			{
				user.roles.map(role => {

					let project = this.state.projects.filter(p => p.id === role.projectId)[0]

				return <TableRow>
					<TableCell>
					{project.name}
					</TableCell>
					<TableCell>
						<Select value={role.role} onChange={(ev) => this.changeRole(user, project, ev)}>
							<MenuItem value="CURATOR">CURATOR</MenuItem>
							<MenuItem value="CONTRIBUTOR">CONTRIBUTOR</MenuItem>
							<MenuItem value="ADMIN">ADMIN</MenuItem>
						</Select>
					</TableCell>
					<TableCell>
						<Button><Delete/></Button>
					</TableCell>
					</TableRow>
				})
			}
			<TableRow>
				<TableCell colSpan={2}>
					<AddProjectDialog projects={this.state.projects} onAdd={(project) => this.onAddProject(user, project)} />
				</TableCell>
				</TableRow>
					</TableBody>
			</Table>
                </TableCell>
                {/* <TableCell align="left">
                    {user.created!.user.name}
                </TableCell>
                <TableCell align="left">
                    {formatDate(user.created!.timestamp)}
                </TableCell> */}
                <TableCell align="left">
                    {/* <IconButton onClick={(e) => { e.preventDefault(); e.stopPropagation(); this.onClickUserSettings(user) }}>
                        <Settings />
                    </IconButton> */}
                </TableCell>
              </TableRow>
            ))}
            <TableRow>
                <TableCell colSpan={3} align="right">
                    <CreateUserDialog onCreate={this.createUser} />
                </TableCell>
            </TableRow>
          </TableBody>
        </Table>
      </TableContainer>
    }

    async fetch() {

        await this.setState(prevState => ({ ...prevState, loading: true }))

	let [ users, projects ] = await Promise.all([
		get<Paginated<User>>(`/v1/users`),
		get<Project[]>(`/v1/projects`),
	])

        this.setState(prevState => ({ ...prevState, users, projects, loading: false }))
    }

    onClickUserSettings = async (user:User) => {
        this.setState(prevState => ({ ...prevState, goToUser: user }))
    }

    createUser = async (user: User) => {

        await post<User>(`/v1/users`, user)

        this.props.onCreateUser()

        this.setState(prevState => ({ ...prevState, showCreateUserDialog: false }))

	this.fetch()
    }


    changeRole = async (user:User, project:Project, ev:any) => {

	for(let r of user.roles) {
		if(r.projectId === project.id) {
			r.role = ev.target.value
			await put<any>(`/v1/projects/${project.id}/users/${user.id}`, {
				user,
				roles: [ r.role]
			})
		}
	}


        this.props.onCreateUser()

        this.setState(prevState => ({ ...prevState, showCreateUserDialog: false }))
    }

    onAddProject = async (user:User, project:Project) => {

	await put<any>(`/v1/projects/${project.id}/users/${user.id}`, {
		user,
		roles: ['ADMIN']
	})
	this.fetch()
    }

}

export default withStyles(styles)(UserList)


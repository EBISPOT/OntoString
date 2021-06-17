

import { Box, Breadcrumbs, Link } from '@material-ui/core'
import React, { Fragment } from 'react'
import { Redirect } from 'react-router-dom'
import { getToken, isLoggedIn } from '../../auth'
import Header from '../../components/Header'
import UserList from './UserList'

interface Props {
    projectId?:string
}

export default class AdminPage extends React.Component<Props> {
	
	constructor(props:Props) {
		super(props)
	}

	render() {
		if (!isLoggedIn()) {
			return <Redirect to='/login' />
		}

		return <Fragment>
			<Header section='admin' projectId={this.props.projectId} />
			<main>
				<UserList onCreateUser={this.onCreateUser} />
			</main>
		</Fragment>
	}

	onCreateUser = () => {
	}

}



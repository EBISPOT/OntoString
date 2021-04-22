

import { Breadcrumbs, Link } from '@material-ui/core'
import React, { Fragment } from 'react'
import { Redirect } from 'react-router-dom'
import { getToken, isLoggedIn } from '../../auth'
import Header from '../../components/Header'
import ProjectList from './ProjectList'

interface Props {
}

export default function ProjectsPage(props:Props) {

    if(!isLoggedIn()) {
        return <Redirect to='/login'/>
    }

    return <Fragment>
        <Header section='projects' />
        <main>
            <ProjectList />
        </main>
    </Fragment>
}


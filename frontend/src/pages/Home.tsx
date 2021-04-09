

import { Breadcrumbs, Link } from '@material-ui/core'
import React from 'react'
import { Redirect } from 'react-router-dom'
import { getToken, isLoggedIn } from '../auth'
import ProjectList from './projects/ProjectList'

interface Props {
}

export default function Home(props:Props) {

    if(!isLoggedIn()) {
        return <Redirect to='/login'/>
    }

    return <div>
        {/* <div>Logged in as {getToken().authEmail} with token {getToken().auth}</div> */}
            <Breadcrumbs>
                <Link color="inherit" href="/">
                    Projects
                </Link>
            </Breadcrumbs>
        <h2>Projects</h2>
        <ProjectList />
    </div>
}


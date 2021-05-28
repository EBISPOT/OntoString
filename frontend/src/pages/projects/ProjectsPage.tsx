

import { Box, Breadcrumbs, Link } from '@material-ui/core'
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
            <Box pt={2} pb={2}>
            <Box fontSize="h5.fontSize">
            Welcome to <b>OntoString</b>, a tool for curating mappings from free text to ontology terms.
            </Box>
            <p>
                OntoString is divided into <b>projects</b>, each of which has its own set of datasources, contexts, and permitted users.  Please select or create a project to continue.
            </p>
            </Box>
            <ProjectList />
        </main>
    </Fragment>
}


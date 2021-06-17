import { Tooltip } from "@material-ui/core"
import React, { Fragment } from "react"
import { Link } from "react-router-dom"
import UserContext from "../UserContext"

export default function Header(props: { section:string, projectId?:string }) {

    let { section, projectId } = props

    return <header style={{ padding: '16px', backgroundColor: 'black', backgroundImage: 'url(\'' + process.env.PUBLIC_URL + '/embl-ebi-background-4.jpg\')', backgroundPosition: '100% 100%' }}>
            <a href={process.env.PUBLIC_URL}>
                <img style={{ height: '100px' }} src={process.env.PUBLIC_URL + "/curator.svg"} />
            </a>
            <nav>
                <ul className="dropdown menu float-left" data-description="navigational" role="menubar" data-dropdown-menu="6mg2ht-dropdown-menu">
                    <li role="menuitem" className={section === 'projects' ? 'active' : ''}><Link to="/projects">Projects</Link></li>
                    {projectId &&
                        <Fragment>
                            <li role="menuitem" className={section === 'entities' ? 'active' : ''}><Link to={`/projects/${projectId}/entities`}>Entities</Link></li>
                            <li role="menuitem" className={section === 'terms' ? 'active' : ''}><Link to={`/projects/${projectId}/terms`}>Terms</Link></li>
                        </Fragment>
                    }
                    {!projectId &&
                        <Fragment>
                            <li role="menuitem" className={'disabled'}>
                                <Tooltip title={<big><big>Please select a project first</big></big>} enterDelay={0}>
                                    <a>Entities</a>
                                </Tooltip>
                            </li>
                            <li role="menuitem" className={'disabled'}>
                                <Tooltip title={<big><big>Please select a project first</big></big>} enterDelay={0}>
                                    <a>Terms</a>
                                </Tooltip>
                            </li>
                        </Fragment>
                    }
                    <li role="menuitem" className={section === 'help' ? 'active' : ''}><Link to={`/help` + (projectId ? '?projectId=' + projectId : '')}>Help</Link></li>
                    <li role="menuitem" className={section === 'about' ? 'active' : ''}><Link to={`/about` + (projectId ? '?projectId=' + projectId : '')}>About</Link></li>
		    <UserContext.Consumer>
			    { user => 
					    <li role="menuitem">{JSON.stringify(user)}</li>
				    }
		    </UserContext.Consumer>
                </ul>
            </nav>
        </header>
}
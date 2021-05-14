import React, { Component, Fragment } from 'react';
import elixir_login_button from '../elixir_logo.png';

import Grid from '@material-ui/core/Grid';
import ElixirAuthService from '../ElixirAuthService';

import { AuthConsumer } from '../auth-context';

import history from "../history";
import { createStyles, Theme, Typography, WithStyles } from '@material-ui/core';
import Link from '@material-ui/core/Link';

import PropTypes from 'prop-types';
import { withStyles } from '@material-ui/core/styles';
import { Lock } from '@material-ui/icons';
import Header from '../components/Header';


const AAP_URL = process.env.REACT_APP_AAPURL;

const elixirRegisterationLink = "https://elixir-europe.org/register";

const elixirLoginContact = <a href="mailto:aai-contact@elixir-europe.org">aai-contact@elixir-europe.org</a>;

const styles = (theme:Theme) => createStyles({
    linkColor: {
        color: '#0000EE',
        '&:visted': {
            color: '#551A8B'
        }
    },
    span: {
        fontWeight: 'bold',
    },
    button: {
        margin: theme.spacing(1),
        color: '#333',
        background: 'linear-gradient(to bottom, #E7F7F9 50%, #D3EFF3 100%)',
        borderRadius: 4,
        border: '1px solid #ccc',
        fontWeight: 'bold',
        textShadow: '0 1px 0 #fff',
        width: 120,
        height: 40,
    },
})

interface Props extends WithStyles<typeof styles> {
    isAuthenticated:boolean
    onAuthenticate:(token:string)=>void
    onLogout:()=>void
}

class Login extends Component<Props> {

    ElixirAuthService:ElixirAuthService

    constructor(props:Props) {
        super(props);

        this.ElixirAuthService = new ElixirAuthService();

        // Check if token is still valid --> Check if working properly!
        // if (this.ElixirAuthService.isTokenExpired(this.token)) {
            // TODO: Add method to refresh token
            // console.log("** Need to refresh token")
        // } else {
            // console.log("** Token is still valid")
            // Set Auth Context 
            // this.props.onAuthenticate(this.ElixirAuthService.getToken());

            // Redirect to Home page if token is still valid
            // history.push("/");
        // }
        // this.handleLogin = this.handleLogin.bind(this);
    }


    handleLogin = (event:any) => {
        this.ElixirAuthService.login();

        if (!this.messageIsAcceptable(event)) {
            return;
        }

        // Store JWT in local storage
        const token = event.data;
        this.ElixirAuthService.setToken(token);

        // Set Auth Context 
        this.props.onAuthenticate(token);

        // Close pop-up login window after token is received
        if (event.source) {
            ((window as any).event!.source).close();
        }

        // Redirect back to page that required a login
        let referrer;
        if (history.location.state && (history.location.state as any).from) {
            referrer = `${process.env.PUBLIC_URL}` + (history.location.state as any).from;
        }

        if (referrer) {
            // history.push(referrer)
            history.replace(`${process.env.PUBLIC_URL}` + (history.location.state as any).from, {
                from: (history.location.state as any).from,
                id: (history.location.state as any).id,
                answer: (history.location.state as any).answer
            })
        } else {
            history.back()
        }
    }

    componentDidMount() {
        window.addEventListener("message", this.handleLogin);
    }

    componentWillUnmount() {
        window.removeEventListener('message', this.handleLogin);
    }

    /**
    * Check if the message is coming from the same domain we use to generate
    * the SSO URL, otherwise it's iffy and shouldn't trust it.
    */
    messageIsAcceptable(event:any) {
        return event.origin === AAP_URL;
    }

    render() {
        const { classes } = this.props;

        return <Fragment>
            <Header section='none' />
            <Grid container
                direction="column"
                justify="space-evenly"
                alignItems="center"
                spacing={3}>
                <Grid item xs={12} sm={6}>
                    <Typography>
                        Please sign in using ELIXIR to access this service.
                    </Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                    <button onClick={this.handleLogin} className={classes.button}>
                        < span >
                            <img src={elixir_login_button} alt="login" style={{ height: '2em', verticalAlign: 'middle', paddingRight: '4px' }} />
                            LOGIN
                        </span>
                    </button>
                </Grid>

                <Grid item xs={12} sm={6}>
                    <Typography>
                        You can use the ELIXIR identity service and other ELIXIR services with the freely available
                        ELIXIR identity, which integrates with Google, ORCID and most academic institutions.

                        Obtain your ELIXIR identity <Link href={elixirRegisterationLink} className={classes.linkColor}>here</Link>.
                    </Typography>
                </Grid>

                <Grid item xs={12} sm={6}>
                    <Typography>
                        If you have problems logging in please contact {elixirLoginContact}.
                    </Typography>
                </Grid>
            </Grid >
        </Fragment>
    }
}

// Login.propTypes = {
//     classes: PropTypes.object.isRequired,
// };

let LoginWithStyles = withStyles(styles)(Login);

export default () => (
    <AuthConsumer>
        {(context) => (
            <LoginWithStyles
                isAuthenticated={context.isAuthenticated}
                onAuthenticate={context.onAuthenticate}
                onLogout={context.onLogout}
            />
        )}
    </AuthConsumer>
)


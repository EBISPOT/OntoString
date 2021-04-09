import React, { createContext } from 'react';
import ElixirAuthService from './ElixirAuthService';

interface AuthContextState {
    isAuthenticated:boolean
    JWTToken:string|null
    onAuthenticate:(token:string)=>void
    onLogout:()=>void
}

export const AuthContext = createContext<AuthContextState>({
    isAuthenticated: false,
    JWTToken: null,
    onAuthenticate: (token:string) => { },
    onLogout: () => { },
});

export class AuthProvider extends React.Component<{}, AuthContextState> {

    elixirAuthService:ElixirAuthService

    constructor() {

        super({});

        this.state = {
            isAuthenticated: false,
            JWTToken: null,
            onAuthenticate: this.onAuthenticate,
            onLogout: this.onLogout
        }

        this.elixirAuthService = new ElixirAuthService();
    }

    componentDidMount() {
        const savedToken = this.elixirAuthService.getToken();

        if (savedToken) {
            this.setState({
                isAuthenticated: true,
                JWTToken: savedToken
            });
        }
    }

    onAuthenticate = (token:string) => {
        this.setState({
            isAuthenticated: true,
            JWTToken: token
        })
    }

    onLogout = () => {
        this.setState({
            isAuthenticated: false,
            JWTToken: null
        })
    }

    getProvidedState():AuthContextState {
        return {
            isAuthenticated: this.state.isAuthenticated,
            JWTToken: this.state.JWTToken,
            onAuthenticate: this.onAuthenticate,
            onLogout: this.onLogout
        };
    }

    render() {
        return (
            <AuthContext.Provider value={this.getProvidedState()} >
                {this.props.children}
            </AuthContext.Provider>
        );
    }
}

export const AuthConsumer = AuthContext.Consumer;


import jwt_decode from 'jwt-decode'; 
import ElixirAuthService from './ElixirAuthService'
import history from './history'

/**
 * Check if user is logged in and if token is still valid and if GDPR accepted.
 */
export function checkUserAuthStatus() {
    const token = getToken().auth;
    // const gdprAccepted = JSON.parse(localStorage.getItem('gdpr-accepted'));
    const eas = new ElixirAuthService();

    if (token && !eas.isTokenExpired(token)) {// && gdprAccepted) {
        return token;
    } else {
        // if (!JSON.parse(gdprAccepted)) {
        //     history.push(`${process.env.PUBLIC_URL}/gdpr`, {
        //         from: `/update-bodyofwork`,
        //     })
        // }
        // else {
        //     history.push(`${process.env.PUBLIC_URL}/login`, {
        //         from: `/update-bodyofwork`,
        //     })
        // }
    }
    return;
}

/**
     * Check for token in local storage
     * and parse out email if token is present.
     */
export function getToken() {
    let token:any = null;
    let userEmail:any = null;
    if (localStorage.getItem('id_token')) {
        token = localStorage.getItem('id_token');
        userEmail = (jwt_decode(token) as any).email;
    }
    return { authEmail: userEmail, auth: token };
}

export function getAuthHeaders() {
    return { 'Authorization': 'Bearer ' + getToken().auth }
    // return { 'Authorization': 'Bearer james' }
}


export function isLoggedIn() {
    return !!checkUserAuthStatus()
}


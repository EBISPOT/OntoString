
import { getAuthHeaders, isLoggedIn } from "./auth"

export async function request(path:string, init?:RequestInit|undefined):Promise<any> {

    if(!isLoggedIn()) {
        window.location.href = process.env.PUBLIC_URL + '/login'
        return
    }

    try {
        let res = await fetch(`${process.env.REACT_APP_APIURL}${path}`, {
            ...(init ? init : {}),
            headers: { ...(init?.headers || {}), ...getAuthHeaders() }
        })

        return await res.json()
    } catch(e) {
        console.dir(e)
        // window.location.href = '/login'
    }

}

export async function get<ResType>(path:string):Promise<ResType> {
    return request(path)
}

export async function post<ReqType, ResType = any>(path:string, body:ReqType):Promise<ResType> {
    return request(path, {
        method: 'POST',
        body: JSON.stringify(body),
        headers: {
            'content-type': 'application/json'
        }
    })
}

export async function put<ReqType, ResType = any>(path:string, body:ReqType):Promise<ResType> {
    return request(path, {
        method: 'PUT',
        body: JSON.stringify(body),
        headers: {
            'content-type': 'application/json'
        }
    })
}
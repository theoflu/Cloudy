import axios from "axios";

export function get(path='', headers = {}){
    return axios.get(path,{ headers: headers }).catch(err=> {
        console.log(err);
        throw new Error("Http Get Error : api.service"+ err);
    } )
}
export function post(path='',params,headers = {}){
    return axios.post(path,params,{ headers: headers }).catch(err=> {
        console.log(err);
        throw new Error("Http Post Error : api.service "+err);
    })
}
export function getFile(path='',params, headers = {}){

    return axios.post(path, params,{ headers: headers }).catch(err=> {
        console.log(err);
        throw new Error("Http Get Error : api.service"+ err);
    } )
}
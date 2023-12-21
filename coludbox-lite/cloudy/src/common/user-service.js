import {get,getFile} from"@/common/api-service"
export function getById(id) {
    return get('user/' + id);
}

export function getAll(headers){
    return get('user/fileslist',headers );
}
export function getFavFile(headers){
    return get('user/favfileslist',headers );
}
export function getTrashFile(headers){
    return get('user/trashfileslist',headers );
}
export function getUser(headers){
    return get('user/getuser',headers);
}

export function getFiles(customHeaders,filename){

    return getFile('user/download/'+ filename,filename,customHeaders);
}

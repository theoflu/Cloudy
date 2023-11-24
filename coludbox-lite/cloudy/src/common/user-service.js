import {get} from"@/common/api-service"
export function getById(id) {
    return get('user/' + id);
}

export function getAll(headers){
    return get('user/fileslist',headers );
}
export function getUser(headers){
    return get('user/getuser',headers);
}
export function getSellerAllProducts(seller){
    return get('products/company/'+ seller)
}

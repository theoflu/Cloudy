import http from "../js/http-common";

class UploadFilesService {
    upload(file, onUploadProgress,jwt) {
        let formData = new FormData();


        formData.append("file", file);
        console.log(formData);
        return http.post("/user/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                "Authorization": "Bearer " +jwt
            },
            onUploadProgress
        });
    }

    getFiles() {
        return http.get("/user/fileslist");
    }
}

export default new UploadFilesService();
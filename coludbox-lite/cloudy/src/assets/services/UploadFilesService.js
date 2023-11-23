import http from "../js/http-common";

class UploadFilesService {
    upload(file, onUploadProgress) {
        let formData = new FormData();


        formData.append("file", file);
        console.log(formData);
        return http.post("/user/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                "Authorization": "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhczNkIiwiaWF0IjoxNzAwNzMwMjQ2LCJleHAiOjE3MDA4MTY2NDZ9.8CHht6XIUMNoq-2vnNUs2FHc1xuRXCEqENM22ua-S5o"
            },
            onUploadProgress
        });
    }

    getFiles() {
        return http.get("/user/fileslist");
    }
}

export default new UploadFilesService();
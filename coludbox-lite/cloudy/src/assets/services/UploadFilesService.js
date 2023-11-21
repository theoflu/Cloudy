import http from "../js/http-common";

class UploadFilesService {
    upload(file, onUploadProgress) {
        let formData = new FormData();


        formData.append("file", file);
        console.log(formData);
        return http.post("/user/upload", formData, {
            headers: {
                "Content-Type": "multipart/form-data",
                "Authorization": "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhczNkIiwiaWF0IjoxNzAwNDY3NDY2LCJleHAiOjE3MDA1NTM4NjZ9.aqG1lnW1wrlXvaTdeEft_vBw7yxt4-1vL3j0ySjJsX4"
            },
            onUploadProgress
        });
    }

    getFiles() {
        return http.get("/user/fileslist");
    }
}

export default new UploadFilesService();
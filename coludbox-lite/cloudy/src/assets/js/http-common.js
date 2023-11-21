import axios from "axios";

export default axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-type": "application/json",
        "Authorization": "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhczNkIiwiaWF0IjoxNzAwNDY3NDY2LCJleHAiOjE3MDA1NTM4NjZ9.aqG1lnW1wrlXvaTdeEft_vBw7yxt4-1vL3j0ySjJsX4"

    }
});
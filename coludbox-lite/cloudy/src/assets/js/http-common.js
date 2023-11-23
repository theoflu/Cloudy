import axios from "axios";

export default axios.create({
    baseURL: "http://localhost:8080",
    headers: {
        "Content-type": "application/json",
        "Authorization": "Bearer " + "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhczNkIiwiaWF0IjoxNzAwNzMwMjQ2LCJleHAiOjE3MDA4MTY2NDZ9.8CHht6XIUMNoq-2vnNUs2FHc1xuRXCEqENM22ua-S5o"

    }
});
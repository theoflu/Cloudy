package com.yasu.ftpLogic.serverStuff;

import org.springframework.cloud.openfeign.FeignClient;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;


@FeignClient(url = "http://localhost:8081/server",name = "server")

public interface ServerRequests {

    @PostMapping("/createDirectories")
    ResponseEntity<?> createDirectories(@RequestParam("path") String path);
}

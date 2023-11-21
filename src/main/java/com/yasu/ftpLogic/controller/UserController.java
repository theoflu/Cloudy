package com.yasu.ftpLogic.controller;

import com.yasu.ftpLogic.configs.JwtUtils;
import com.yasu.ftpLogic.dto.UploadRequest;
import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.FileDetail;
import com.yasu.ftpLogic.entity.UserEntity;
import com.yasu.ftpLogic.errorHandling.ErrorMessage;
import com.yasu.ftpLogic.errorHandling.statusProcesses;
import com.yasu.ftpLogic.ftpStuff.Client;
import com.yasu.ftpLogic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController

@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;


    @PostMapping("/upload")
    public statusProcesses upload(@RequestHeader("Authorization")String token, @RequestParam("file") MultipartFile file){
        try {
            // client giricek tüm işlemlerini bitirdikten sonra çıkıcak şekilde düzeltelim
            Client client = new Client("localhost", 3456);
            File tempFile = File.createTempFile("uploadedFile", file.getOriginalFilename());
            try (OutputStream os = new FileOutputStream(tempFile)) {
                os.write(file.getBytes());
            }
            String username = jwtUtils.getUserNameFromJwtToken(token.substring(6));

           ErrorMessage isGiddimi= client.sendFile(tempFile.getAbsolutePath(), username);
           if(isGiddimi.getCode().equals("200")){
               return new statusProcesses("GÖNDERİLDİ");
           }
           else
               return new statusProcesses("Gönderilemedi");


        }
        catch (IOException e){
            throw new RuntimeException(e);
        }

    }
    @GetMapping("/fileslist")
    public List<FileDetail> filesList(){
        Client client  = new Client("localhost", 3456);
       // String username= jwtUtils.getUserNameFromJwtToken(token.substring(6));
        client.listFiles("C:\\user\\"+"as3d");
        return client.listFiles("C:\\user\\"+"as3d");
    }
    public ResponseEntity<?> download(String username){
        Client client  = new Client("localhost", 3456);
        // String username= jwtUtils.getUserNameFromJwtToken(token.substring(6));
        client.downloadFile("C:\\user\\",username);
        return ResponseEntity.ok(client.listFiles("C:\\user\\"+"as3d"));
    }

    @PostMapping("/signUp")
    public ResponseEntity<?> signUp(@RequestBody UserEntity userEntity){

        return ResponseEntity.ok(userService.signUp(userEntity));

    }
    @PostMapping("/signIn")
    public ResponseEntity<?> signIn(@RequestBody UserEntity userEntity){

        return ResponseEntity.ok(userService.signIn(userEntity));

    }
    @PostMapping("/delete/{id}")
    public ResponseEntity<?> signIn(@PathVariable Long id){

        return ResponseEntity.ok(userService.delete(id));

    }

    @GetMapping("/list")
    public ResponseEntity<List<UserDto>> userList(){
        return ResponseEntity.ok(userService.userList());
    }
}

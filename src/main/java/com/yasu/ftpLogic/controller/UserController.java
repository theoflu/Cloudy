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

import java.io.*;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin(origins = "*", maxAge = 3600,allowedHeaders = "*")
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
            File tempFile = File.createTempFile("uploaded", file.getOriginalFilename());
            try (InputStream inputStream = file.getInputStream();
                 OutputStream outputStream = new FileOutputStream(tempFile)) {
                byte[] buffer = new byte[1024];
                int bytesRead;
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }
            }
            String username = jwtUtils.getUserNameFromJwtToken(token.substring(6));


           ErrorMessage isGiddimi= client.sendFile(tempFile.getAbsolutePath(), username);

           if(isGiddimi.getCode().equals("200")){
               userService.updateCapacity(username,isGiddimi.getUsageSize());
               if (tempFile != null && tempFile.exists()) {
                   tempFile.delete();
               }
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
    public List<FileDetail> filesList(@RequestHeader("Authorization")String token){
        Client client  = new Client("localhost", 3456);
        String username= jwtUtils.getUserNameFromJwtToken(token.substring(6));
        client.listFiles("C:\\user\\"+username);
        return client.listFiles("C:\\user\\"+username);
    }
    @GetMapping("/getuser")
    public UserEntity getUser(@RequestHeader("Authorization")String token){
        String username= jwtUtils.getUserNameFromJwtToken(token.substring(6));
        return  userService.getUser(username);
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

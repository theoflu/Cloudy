package com.yasu.ftpLogic.controller;

import com.yasu.ftpLogic.configs.JwtUtils;
import com.yasu.ftpLogic.dto.UploadRequest;
import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.UserEntity;
import com.yasu.ftpLogic.errorHandling.statusProcesses;
import com.yasu.ftpLogic.ftpStuff.Client;
import com.yasu.ftpLogic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.ArrayList;
import java.util.List;

@CrossOrigin("*")
@RestController

@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final JwtUtils jwtUtils;


    @GetMapping("/upload")
    public statusProcesses upload(@RequestHeader("Authorization")String token, @RequestBody UploadRequest uploadRequest){
        // client giricek tüm işlemlerini bitirdikten sonra çıkıcak şekilde düzeltelim
        Client client  = new Client("localhost", 3456);
        String username= jwtUtils.getUserNameFromJwtToken(token.substring(6));
        List<String> isGiddimi= new ArrayList<>();
        for(int i=0;i<uploadRequest.getFilepath().size();i++){
            isGiddimi.add(client.sendFile(uploadRequest.getFilepath().get(i), username).getMessage());

        }
       return new statusProcesses(isGiddimi);




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

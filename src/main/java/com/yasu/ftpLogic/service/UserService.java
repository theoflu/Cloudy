package com.yasu.ftpLogic.service;

import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.UserEntity;

import java.util.List;

public interface UserService {
    UserDto signUp(UserEntity userEntity);
    String signIn(UserEntity entity);

    String delete(Long id);
    List<UserDto> userList();
}

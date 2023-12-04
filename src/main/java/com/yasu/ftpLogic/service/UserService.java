package com.yasu.ftpLogic.service;

import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.FavouriteFile;
import com.yasu.ftpLogic.entity.FileDetail;
import com.yasu.ftpLogic.entity.UserEntity;
import com.yasu.ftpLogic.entity.UserFileEntitiy;

import java.util.List;

public interface UserService {
    UserDto signUp(UserEntity userEntity);
    String signIn(UserEntity entity);
    UserEntity getUser(String username);
    String delete(Long id);
    List<UserDto> userList();
   double updateCapacity(String username,FileDetail fileDetail,String filename,long capacity);

   void updateFavourite (String username, FavouriteFile favouriteFile);
   List<UserFileEntitiy> userFileList(String username);
}

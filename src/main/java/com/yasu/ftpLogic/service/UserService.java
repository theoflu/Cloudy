package com.yasu.ftpLogic.service;

import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.*;

import java.util.List;

public interface UserService {
    void saveTrashcan(String username , List<FileDetail> fileDetail);
    UserDto signUp(UserEntity userEntity);
    String signIn(UserEntity entity);
    UserEntity getUser(String username);
    String delete(Long id);
    List<UserDto> userList();
   double updateCapacity(String username,FileDetail fileDetail,String filename,long capacity);

   void updateFavourite (String username, FavouriteFile favouriteFile);
   List<UserFileEntitiy> userFileList(String username);
}

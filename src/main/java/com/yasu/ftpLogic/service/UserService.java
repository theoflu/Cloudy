package com.yasu.ftpLogic.service;

import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.*;

import java.util.List;
import java.util.Optional;

public interface UserService {
    void saveTrashcan(String username , List<FileDetail> fileDetail,String filename);
    UserDto signUp(UserEntity userEntity);
    String signIn(UserEntity entity);
    UserEntity getUser(String username);
    String delete(String username, String filename);
    List<UserDto> userList();
   double saveUserFile(String username,FileDetail fileDetail,String filename,long capacity);
   void updateCapacity(String username, String filename,long capacity);
   void updateFavourite (String username, FavouriteFile favouriteFile);
   List<UserFileEntitiy> userFileList(String username);
   List<UserFileEntitiy> userFavFileList(String username);
   List<UserFileEntitiy> userTrashFileList(String username);

    Boolean isFavourite(String username ,String filename);
}

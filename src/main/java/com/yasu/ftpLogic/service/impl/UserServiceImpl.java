package com.yasu.ftpLogic.service.impl;

import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.*;
import com.yasu.ftpLogic.ftpStuff.Client;
import com.yasu.ftpLogic.repository.FilesRepository;
import com.yasu.ftpLogic.repository.TrashCanRepository;
import com.yasu.ftpLogic.repository.UserRepository;
import com.yasu.ftpLogic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
    private final FilesRepository filesRepository;
    private final TrashCanRepository trashCanRepository;

    @Override
    @Transactional
    public void saveTrashcan(String username ,List<FileDetail> fileDetail,String filename){
        UserEntity userr= userRepository.findUserEntityByUsername(username);
        UserFileEntitiy files = filesRepository.findByUseridAndFilename(userr.getId(), filename);
        if(files.isTrashCanFiles()){
            files.setTrashCanFiles(false);
            files.setFavourite(false);
            trashCanRepository.findByUsername(username).ifPresentOrElse(user-> {
                        user.setFileDetail(fileDetail);
                        trashCanRepository.save(user);
                    } ,
                    ()->
                    {
                        trashCanRepository.save(TrashCanFiles.builder()
                                .username(username)
                                .fileDetail(fileDetail)
                                .build());

                    }

            );

        }
        else{
            files.setTrashCanFiles(true);
            files.setFavourite(false);
            trashCanRepository.findByUsername(username).ifPresentOrElse(user-> {
                        user.setFileDetail(fileDetail);
                        trashCanRepository.save(user);
                    } ,
                    ()->
                    {
                        trashCanRepository.save(TrashCanFiles.builder()
                                .username(username)
                                .fileDetail(fileDetail)
                                .build());

                    }

            );

        }
        filesRepository.save(files);
    }
    @Override
    public UserDto signUp(UserEntity userEntity) {
        modelMapper.map(userRepository.save(userEntity), UserDto.class);
        userEntity.setPassword(passwordEncoder.encode(userEntity.getPassword()));
        userRepository.save(userEntity);
        return    modelMapper.map(userRepository.save(userEntity), UserDto.class);
    }

    @Override
    public String signIn(UserEntity userEntity) {
        UserEntity entity= userRepository.findByEmail(userEntity.getEmail());
        if(passwordEncoder.matches(userEntity.getPassword(),entity.getPassword()))
        {
            return "Başarılı";
        }
        return "Başarısız";
    }

    @Override
    public UserEntity getUser(String username) {
        return  userRepository.findUserEntityByUsername(username);
    }

    @Override
    public String delete(String username, String filename) {
        UserEntity user=userRepository.findUserEntityByUsername(username);
        filesRepository.deleteByUseridAndFilename(user.getId(),filename);
        return "Silindi";
    }

    @Override
    public List<UserDto> userList() {
        List<UserEntity> users=userRepository.findAll();
        return users.stream().map(user->modelMapper.map(user,UserDto.class)).collect(Collectors.toList());
    }
    @Override
    public void updateCapacity(String username, String filename,long capacity) {
        UserEntity user= userRepository.findUserEntityByUsername(username);
        user.setUsageSize(capacity/1000000000d);
        userRepository.save(user);
    }

    @Override
    public double saveUserFile(String username, FileDetail fileDetail, String filename,long capacity) {
        UserEntity user= userRepository.findUserEntityByUsername(username);
        user.setUsageSize(capacity/1000000000d);
        UserFileEntitiy users=UserFileEntitiy.
                builder()
                .userid(user.getId())
                .isFavourite(false)
                .filename(filename)
                .fileDetail(fileDetail)
                .trashCanFiles(false)
                .build();
        Optional<Boolean> a=filesRepository.findByFilenameAndUserid(filename,user.getId());
       if(a.isEmpty()) {
           filesRepository.save(users);
       }



       userRepository.save(user);
        return user.getUsageSize();
    }

    @Override
    public void updateFavourite (String username, FavouriteFile favouriteFile) {
        UserEntity user= userRepository.findUserEntityByUsername(username);
        UserFileEntitiy files = filesRepository.findByUseridAndFilename(user.getId(), favouriteFile.getFilename());
        files.setFavourite(favouriteFile.getIsCheck());
        filesRepository.save(files);

    }

    @Override
    public List<UserFileEntitiy> userFileList(String username) {
        UserEntity user= userRepository.findUserEntityByUsername(username);
        List<UserFileEntitiy> files= filesRepository.findByUserid(user.getId());
        return files;

    }
}

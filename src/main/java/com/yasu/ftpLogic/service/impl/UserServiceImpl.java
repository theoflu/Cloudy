package com.yasu.ftpLogic.service.impl;

import com.yasu.ftpLogic.dto.UserDto;
import com.yasu.ftpLogic.entity.UserEntity;
import com.yasu.ftpLogic.repository.UserRepository;
import com.yasu.ftpLogic.service.UserService;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;
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
            return "Başarlı";
        }
        return "Başarsoz";
    }

    @Override
    public UserEntity getUser(String username) {
        return  userRepository.findUserEntityByUsername(username);
    }

    @Override
    public String delete(Long id) {
        userRepository.deleteById(id);
        return "Silindi";
    }

    @Override
    public List<UserDto> userList() {
        List<UserEntity> users=userRepository.findAll();
        return users.stream().map(user->modelMapper.map(user,UserDto.class)).collect(Collectors.toList());
    }

    @Override
    public double updateCapacity(String username,Long size) {
        UserEntity user= userRepository.findUserEntityByUsername(username);
        user.setUsageSize(size/1000000000d);
        userRepository.save(user);
        return user.getUsageSize();
    }
}

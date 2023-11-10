package com.yasu.ftpLogic.dto;

import jakarta.persistence.Column;
import lombok.Data;

@Data

public class UserDto {

    private String username;
    private String email;
}

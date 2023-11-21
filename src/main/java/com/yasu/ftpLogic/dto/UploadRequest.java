package com.yasu.ftpLogic.dto;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;

@Data

public class UploadRequest {
    private List<String> filepath;

}

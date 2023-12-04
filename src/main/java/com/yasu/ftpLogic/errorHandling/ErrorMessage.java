package com.yasu.ftpLogic.errorHandling;

import com.yasu.ftpLogic.entity.FileDetail;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ErrorMessage {
    private String Message;
    private String code;
    private FileDetail fileDetail;
    private long capacity;



}

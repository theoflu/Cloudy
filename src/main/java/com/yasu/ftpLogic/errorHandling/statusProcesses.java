package com.yasu.ftpLogic.errorHandling;

import com.yasu.ftpLogic.entity.UserFileEntitiy;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class statusProcesses  {
   private List<UserFileEntitiy> files;
    private  String processes;
}

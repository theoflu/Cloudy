package com.yasu.ftpLogic.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class FileDetail {
    private String filename;
    private String filepath;
    private long filesize;
    private long lastmodified;
    private String mediType;

}

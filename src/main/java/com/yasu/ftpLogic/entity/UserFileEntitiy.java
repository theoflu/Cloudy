package com.yasu.ftpLogic.entity;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.elasticsearch.annotations.Document;

import java.util.List;

@Data
@Document(indexName = "files")
@EqualsAndHashCode(of="id")
@Builder
public class UserFileEntitiy {
    @Id
    private String id;
    private long userid;
    private boolean isFavourite;
    private String filename;
    private FileDetail fileDetail;


}

package com.yasu.ftpLogic.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.elasticsearch.annotations.Document;

import java.io.File;
import java.util.List;

@Data
@AllArgsConstructor
@Document(indexName = "trash-files")
@EqualsAndHashCode(of="id")
@NoArgsConstructor
@Builder
public class TrashCanFiles {
    @Id
    private String id;
    private String username;
    private List<FileDetail> fileDetail;
}

package com.yasu.ftpLogic.repository;


import com.yasu.ftpLogic.entity.UserFileEntitiy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;
import java.util.Optional;

public interface FilesRepository extends ElasticsearchRepository<UserFileEntitiy,Long> {
    UserFileEntitiy findByUseridAndFilename(Long id,String filename);
    Optional<Boolean> findByFilenameAndUserid(String filename, Long id);
    List<UserFileEntitiy> findByUserid(Long id);
}

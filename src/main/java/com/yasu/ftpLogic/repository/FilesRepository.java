package com.yasu.ftpLogic.repository;


import com.yasu.ftpLogic.entity.UserFileEntitiy;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface FilesRepository extends ElasticsearchRepository<UserFileEntitiy,Long> {
    UserFileEntitiy findByUseridAndFilename(Long id,String filename);
}

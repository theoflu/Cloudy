package com.yasu.ftpLogic.repository;


import com.yasu.ftpLogic.entity.TrashCanFiles;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import java.util.Optional;


public interface TrashCanRepository extends ElasticsearchRepository<TrashCanFiles,String> {
 Optional<TrashCanFiles> findByUsername(String username);
}

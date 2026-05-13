package com.mlplatform.repository;

import com.mlplatform.model.ModelVersion;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelRepository extends MongoRepository<ModelVersion, String> {
    List<ModelVersion> findByName(String name);
    List<ModelVersion> findByStatus(String status);
}
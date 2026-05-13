package com.mlplatform.repository;

import com.mlplatform.model.Experiment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExperimentRepository extends MongoRepository<Experiment, String> {
    List<Experiment> findByStatus(String status);
    List<Experiment> findByUserId(String userId);
    List<Experiment> findByNameContaining(String name);
}
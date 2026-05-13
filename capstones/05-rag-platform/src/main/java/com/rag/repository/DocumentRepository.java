package com.rag.repository;

import com.rag.model.DocumentMetadata;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends MongoRepository<DocumentMetadata, String> {
    List<DocumentMetadata> findByStatus(String status);
    List<DocumentMetadata> findByFileNameContaining(String fileName);
}
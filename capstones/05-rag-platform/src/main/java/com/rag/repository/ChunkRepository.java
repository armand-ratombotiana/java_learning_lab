package com.rag.repository;

import com.rag.model.TextChunk;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChunkRepository extends MongoRepository<TextChunk, String> {
    List<TextChunk> findByDocumentId(String documentId);
    List<TextChunk> findByDocumentIdOrderByChunkIndex(String documentId);
}
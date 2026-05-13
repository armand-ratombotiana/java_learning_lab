package com.rag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Document(collection = "chunks")
public class TextChunk {

    @Id
    private String id;
    private String documentId;
    private String content;
    private int chunkIndex;
    private List<Float> embedding;
    private String metadata;
    private int tokenCount;

    public TextChunk() {}

    public TextChunk(String documentId, String content, int chunkIndex) {
        this.documentId = documentId;
        this.content = content;
        this.chunkIndex = chunkIndex;
        this.tokenCount = content.split("\\s+").length;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getDocumentId() { return documentId; }
    public void setDocumentId(String documentId) { this.documentId = documentId; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public int getChunkIndex() { return chunkIndex; }
    public void setChunkIndex(int chunkIndex) { this.chunkIndex = chunkIndex; }
    public List<Float> getEmbedding() { return embedding; }
    public void setEmbedding(List<Float> embedding) { this.embedding = embedding; }
    public String getMetadata() { return metadata; }
    public void setMetadata(String metadata) { this.metadata = metadata; }
    public int getTokenCount() { return tokenCount; }
    public void setTokenCount(int tokenCount) { this.tokenCount = tokenCount; }
}
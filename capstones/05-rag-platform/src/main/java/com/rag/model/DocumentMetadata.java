package com.rag.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
import java.util.Map;

@Document(collection = "documents")
public class DocumentMetadata {

    @Id
    private String id;
    private String fileName;
    private String contentType;
    private long fileSize;
    private List<String> chunks;
    private Map<String, String> metadata;
    private String status;
    private long uploadedAt;
    private long processedAt;

    public DocumentMetadata() {
        this.uploadedAt = System.currentTimeMillis();
        this.status = "UPLOADED";
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFileName() { return fileName; }
    public void setFileName(String fileName) { this.fileName = fileName; }
    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }
    public long getFileSize() { return fileSize; }
    public void setFileSize(long fileSize) { this.fileSize = fileSize; }
    public List<String> getChunks() { return chunks; }
    public void setChunks(List<String> chunks) { this.chunks = chunks; }
    public Map<String, String> getMetadata() { return metadata; }
    public void setMetadata(Map<String, String> metadata) { this.metadata = metadata; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public long getUploadedAt() { return uploadedAt; }
    public void setUploadedAt(long uploadedAt) { this.uploadedAt = uploadedAt; }
    public long getProcessedAt() { return processedAt; }
    public void setProcessedAt(long processedAt) { this.processedAt = processedAt; }
}
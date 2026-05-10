# Vector Database Projects - Module 77

This module covers vector stores including Pinecone, Milvus, Chroma, and semantic search using embeddings.

## Mini-Project: Semantic Search with Embeddings (2-4 hours)

### Overview
Build a Java application for semantic search using vector embeddings. This project demonstrates embedding generation, vector storage, and similarity search.

### Project Structure
```
vector-db-mini/
├── pom.xml
├── src/main/java/com/learning/vectordb/
│   ├── MiniProjectApplication.java
│   ├── service/
│   │   ├── EmbeddingService.java
│   │   └── VectorSearchService.java
│   └── model/
│       └── SearchResult.java
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>vector-db-mini</artifactId>
    <version>1.0.0</version>
    <name>Vector Database Mini Project</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-ai.version>1.0.0-M2</spring-ai.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-data-redis</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-spring-boot-starter</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### Embedding Service
```java
package com.learning.vectordb.service;

import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class EmbeddingService {
    
    private static final Logger logger = LoggerFactory.getLogger(EmbeddingService.class);
    
    private final EmbeddingClient embeddingClient;
    
    public EmbeddingService(EmbeddingClient embeddingClient) {
        this.embeddingClient = embeddingClient;
    }
    
    public float[] embed(String text) {
        EmbeddingResponse response = embeddingClient.embed(text);
        
        return response.contents().get(0).vectorAsList().stream()
            .map(Float::floatValue)
            .toArray(float[]::new);
    }
    
    public List<float[]> embedAll(List<String> texts) {
        EmbeddingResponse response = embeddingClient.embed(texts);
        
        return response.contents().stream()
            .map(c -> c.vectorAsList().stream()
                .map(Float::floatValue)
                .toArray(float[]::new))
            .toList();
    }
    
    public float cosineSimilarity(float[] a, float[] b) {
        float dotProduct = 0;
        float normA = 0;
        float normB = 0;
        
        for (int i = 0; i < a.length; i++) {
            dotProduct += a[i] * b[i];
            normA += a[i] * a[i];
            normB += b[i] * b[i];
        }
        
        return dotProduct / (float) Math.sqrt(normA * normB);
    }
}
```

#### Vector Search Service
```java
package com.learning.vectordb.service;

import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class VectorSearchService {
    
    private static final Logger logger = LoggerFactory.getLogger(VectorSearchService.class);
    
    private final Map<String, VectorEntry> vectorStore;
    private final EmbeddingService embeddingService;
    
    public VectorSearchService(EmbeddingService embeddingService) {
        this.vectorStore = new ConcurrentHashMap<>();
        this.embeddingService = embeddingService;
    }
    
    public void addDocument(String id, String content) {
        float[] embedding = embeddingService.embed(content);
        
        vectorStore.put(id, new VectorEntry(id, content, embedding));
        
        logger.info("Added document: {}", id);
    }
    
    public List<SearchResult> search(String query, int topK) {
        float[] queryEmbedding = embeddingService.embed(query);
        
        List<SearchResult> results = new ArrayList<>();
        
        for (VectorEntry entry : vectorStore.values()) {
            float similarity = embeddingService.cosineSimilarity(
                queryEmbedding, entry.embedding);
            
            results.add(new SearchResult(entry.id, entry.content, similarity));
        }
        
        results.sort((a, b) -> Float.compare(b.similarity, a.similarity));
        
        return results.stream().limit(topK).toList();
    }
    
    public void delete(String id) {
        vectorStore.remove(id);
        logger.info("Deleted document: {}", id);
    }
}

class VectorEntry {
    final String id;
    final String content;
    final float[] embedding;
    
    VectorEntry(String id, String content, float[] embedding) {
        this.id = id;
        this.content = content;
        this.embedding = embedding;
    }
}

class SearchResult {
    final String id;
    final String content;
    final float similarity;
    
    SearchResult(String id, String content, float similarity) {
        this.id = id;
        this.content = content;
        this.similarity = similarity;
    }
}
```

### Build and Run

```bash
mvn clean package
java -jar target/vector-db-mini-1.0.0.jar
```

### Extension Ideas
1. Add Pinecone integration
2. Add Milvus support
3. Implement Chroma client
4. Add pagination
5. Implement filters

---

## Real-World Project: Enterprise RAG System with Vector Store (8+ hours)

### Project Structure
```
enterprise-vector-search/
├── pom.xml
├── src/main/java/com/learning/vectordb/
│   ├── VectorSearchApplication.java
│   ├── service/
│   │   ├── VectorStoreService.java
│   │   ├── HybridSearchService.java
│   │   └── CollectionService.java
│   ├── controller/
│   │   └── VectorController.java
```

### Implementation

#### pom.xml
```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 
         http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>
    <groupId>com.learning</groupId>
    <artifactId>enterprise-vector-search</artifactId>
    <version>1.0.0</version>
    <name>Enterprise Vector Search</name>
    
    <parent>
        <groupId>org.springframework.boot</groupId>
        <artifactId>spring-boot-starter-parent</artifactId>
        <version>3.2.0</version>
    </parent>
    
    <properties>
        <java.version>21</java.version>
        <spring-ai.version>1.0.0-M2</spring-ai.version>
    </properties>
    
    <dependencies>
        <dependency>
            <groupId>org.springframework.boot</groupId>
            <artifactId>spring-boot-starter-web</artifactId>
        </dependency>
        <dependency>
            <groupId>org.springframework.ai</groupId>
            <artifactId>spring-ai-spring-boot-starter</artifactId>
            <version>${spring-ai.version}</version>
        </dependency>
    </dependencies>
</project>
```

#### Vector Store Service
```java
package com.learning.vectordb.service;

import org.springframework.ai.vectorstore.*;
import org.springframework.ai.embedding.EmbeddingClient;
import org.springframework.ai.document.*;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

@Service
public class VectorStoreService {
    
    private static final Logger logger = LoggerFactory.getLogger(VectorStoreService.class);
    
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public VectorStoreService(VectorStore vectorStore, EmbeddingClient embeddingClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }
    
    public void addDocuments(List<Document> documents) {
        vectorStore.add(documents);
        logger.info("Added {} documents", documents.size());
    }
    
    public List<Document> similaritySearch(String query, int topK) {
        return vectorStore.similaritySearch(query, topK);
    }
    
    public List<Document> similaritySearchWithThreshold(String query, int topK, double threshold) {
        List<Document> results = vectorStore.similaritySearch(query, topK);
        
        return results.stream()
            .filter(d -> {
                Double score = (Double) d.getMetadata().get("score");
                return score == null || score >= threshold;
            })
            .toList();
    }
    
    public void delete(Collection<String> ids) {
        vectorStore.delete(ids);
    }
}
```

### Build and Run

```bash
mvn clean package
java -jar target/enterprise-vector-search-1.0.0.jar
```

### Extension Ideas
1. Add multi-collection support
2. Implement vector compression
3. Add approximate nearest neighbors
4. Implement metadata filtering
5. Add real-time indexing
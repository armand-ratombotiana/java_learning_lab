# Deep Dive: Vector Databases for AI Applications

## Table of Contents
1. [Introduction to Vector Databases](#introduction)
2. [Vector Embeddings](#embeddings)
3. [Similarity Search](#similarity)
4. [Database Systems](#systems)
5. [RAG Implementation](#rag)
6. [Integration with Java](#java)
7. [Best Practices](#best-practices)

---

## 1. Introduction to Vector Databases

Vector databases are specialized databases designed to store and efficiently search high-dimensional vectors (embeddings). They are fundamental to modern AI applications including:

- **Semantic Search**: Find documents by meaning, not keywords
- **Recommendation Systems**: Similar item discovery
- **RAG Systems**: Context retrieval for LLMs
- **Anomaly Detection**: Find outliers in embeddings

### Why Specialized Databases?

Traditional databases cannot efficiently perform similarity search on high-dimensional vectors. Vector databases use:
- **Approximate Nearest Neighbor (ANN)** algorithms
- **Index structures** optimized for vector operations
- **Compression techniques** for memory efficiency

---

## 2. Vector Embeddings

### What are Embeddings?

Embeddings are numerical representations of data (text, images, audio) in a high-dimensional vector space where similar items are close together.

### Creating Embeddings

```java
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.openai.OpenAiEmbeddingModel;

public class EmbeddingCreation {
    
    public static void main(String[] args) {
        EmbeddingModel model = OpenAiEmbeddingModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .modelName("text-embedding-ada-002")
            .build();
        
        // Create embedding
        Embedding embedding = model.embed("Hello world").content();
        
        System.out.println("Embedding dimension: " + embedding.size());
    }
}
```

### Embedding Models

| Model | Provider | Dimensions | Use Case |
|-------|----------|------------|----------|
| text-embedding-ada-002 | OpenAI | 1536 | General purpose |
| text-embedding-3-small | OpenAI | 1536 | Faster, cheaper |
| BGE-small | HuggingFace | 384 | Local, free |
| GTR-t5 | Google | 768 | High quality |

---

## 3. Similarity Search

### Distance Metrics

```java
import dev.langchain4j.data.embedding.Embedding;

public class DistanceMetrics {
    
    // Cosine similarity
    public static double cosineSimilarity(Embedding a, Embedding b) {
        double dotProduct = 0;
        double normA = 0;
        double normB = 0;
        
        for (int i = 0; i < a.size(); i++) {
            dotProduct += a.get(i) * b.get(i);
            normA += a.get(i) * a.get(i);
            normB += b.get(i) * b.get(i);
        }
        
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }
    
    // Euclidean distance
    public static double euclideanDistance(Embedding a, Embedding b) {
        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            double diff = a.get(i) - b.get(i);
            sum += diff * diff;
        }
        return Math.sqrt(sum);
    }
    
    // Dot product
    public static double dotProduct(Embedding a, Embedding b) {
        double sum = 0;
        for (int i = 0; i < a.size(); i++) {
            sum += a.get(i) * b.get(i);
        }
        return sum;
    }
}
```

### Similarity Search Implementation

```java
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.store.embedding.inmemory.InMemoryEmbeddingStore;
import dev.langchain4j.data.segment.TextSegment;

public class SimilaritySearch {
    
    public static void main(String[] args) {
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        
        // Add documents
        addDocument(store, embeddingModel, 
            "Java is a programming language for enterprise applications");
        addDocument(store, embeddingModel,
            "Python is great for data science and machine learning");
        addDocument(store, embeddingModel,
            "JavaScript is the language of the web");
        
        // Search
        Embedding query = embeddingModel.embed(
            "Tell me about programming").content();
        
        List<EmbeddingMatch<TextSegment>> results = 
            store.findRelevant(query, 3);
        
        for (EmbeddingMatch<TextSegment> match : results) {
            System.out.println("Score: " + match.score());
            System.out.println("Text: " + match.embedded().text());
        }
    }
    
    private static void addDocument(
            EmbeddingStore<TextSegment> store,
            EmbeddingModel model,
            String text) {
        Embedding embedding = model.embed(text).content();
        store.add(embedding, TextSegment.from(text));
    }
}
```

---

## 4. Vector Database Systems

### 4.1 Pinecone

```yaml
# Configuration
spring:
  ai:
    vectorstore:
      pinecone:
        api-key: ${PINECONE_API_KEY}
        environment: us-west1
        project-id: YOUR_PROJECT
```

```java
@Bean
public VectorStore pineconeVectorStore(
        EmbeddingClient embeddingClient,
        @Value("${spring.ai.vectorstore.pinecone.api-key}") String apiKey) {
    
    return new PineconeVectorStore(embeddingClient, apiKey);
}
```

### 4.2 Weaviate

```java
WeaviateVectorStore store = WeaviateVectorStore.builder()
    .schema("Article")
    .url("http://localhost:8080")
    .build();
```

### 4.3 Chroma (Local)

```java
// In-memory or local file-based
ChromaVectorStore store = ChromaVectorStore.builder()
    .build();
```

### 4.4 Milvus

```java
MilvusVectorStore store = MilvusVectorStore.builder()
    .host("localhost")
    .port(19530)
    .build();
```

### Comparison

| Database | Type | Cloud | Scalability | Best For |
|----------|------|-------|-------------|----------|
| Pinecone | SaaS | Yes | Excellent | Production |
| Weaviate | Both | Yes | Good | Flexible schema |
| Chroma | Local | No | Limited | Development |
| Milvus | Open Source | Both | Excellent | Large scale |

---

## 5. RAG Implementation

### Complete RAG Pipeline

```java
import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.model.openai.OpenAiChatModel;
import dev.langchain4j.service.AiServices;
import dev.langchain4j.rag.DefaultRetrievalAugmentor;
import dev.langchain4j.store.embedding.EmbeddingStore;
import dev.langchain4j.data.document.loader.FileSystemDocumentLoader;
import dev.langchain4j.data.document.splitter.DocumentSplitters;

public class CompleteRAG {
    
    interface RagAssistant {
        String answer(String question);
    }
    
    public static void main(String[] args) throws Exception {
        // 1. Load documents
        List<Document> documents = FileSystemDocumentLoader
            .loadDocuments(Path.of("docs")));
        
        // 2. Chunk documents
        DocumentSplitter splitter = DocumentSplitters
            .byParagraph(500, 50);
        List<TextSegment> segments = splitter.split(documents);
        
        // 3. Create embeddings
        EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();
        EmbeddingStore<TextSegment> store = new InMemoryEmbeddingStore<>();
        
        for (TextSegment segment : segments) {
            Embedding embedding = embeddingModel
                .embed(segment.text()).content();
            store.add(embedding, segment);
        }
        
        // 4. Build RAG assistant
        ChatLanguageModel chatModel = OpenAiChatModel.builder()
            .apiKey(System.getenv("OPENAI_API_KEY"))
            .build();
        
        RetrievalAugmentor augmentor = DefaultRetrievalAugmentor.builder()
            .embeddingModel(embeddingModel)
            .embeddingStore(store)
            .build();
        
        RagAssistant assistant = AiServices.builder(RagAssistant.class)
            .chatLanguageModel(chatModel)
            .retrievalAugmentor(augmentor)
            .build();
        
        // 5. Answer questions
        String answer = assistant.answer(
            "What is the main topic of the documents?");
        System.out.println(answer);
    }
}
```

---

## 6. Java Integration

### Spring AI Integration

```java
@Service
public class DocumentService {
    
    private final VectorStore vectorStore;
    private final EmbeddingClient embeddingClient;
    
    public DocumentService(
            VectorStore vectorStore,
            EmbeddingClient embeddingClient) {
        this.vectorStore = vectorStore;
        this.embeddingClient = embeddingClient;
    }
    
    public void addDocument(String text) {
        Document doc = new Document(text);
        vectorStore.add(List.of(doc));
    }
    
    public List<Document> search(String query, int topK) {
        return vectorStore.similaritySearch(
            SearchRequest.query(query).topK(topK)
        );
    }
}
```

### LangChain4j Integration

```java
@Service
public class VectorService {
    
    private final EmbeddingStore<TextSegment> store;
    private final EmbeddingModel model;
    
    public void store(String id, String text) {
        Embedding embedding = model.embed(text).content();
        store.add(id, embedding, TextSegment.from(text));
    }
    
    public List<SearchResult> search(String query) {
        Embedding queryEmbedding = model.embed(query).content();
        return store.findRelevant(queryEmbedding, 10);
    }
}
```

---

## 7. Best Practices

### Chunking Strategy

```java
public class ChunkingStrategy {
    
    public static List<TextSegment> chunkByParagraph(
            String text, 
            int chunkSize, 
            int overlap) {
        
        DocumentSplitter splitter = DocumentSplitters
            .byParagraph(chunkSize, overlap);
        
        return splitter.split(new Document(text));
    }
    
    public static List<TextSegment> chunkBySentence(
            String text,
            int maxSentences) {
        
        DocumentSplitter splitter = DocumentSplitters
            .bySentence(maxSentences);
        
        return splitter.split(new Document(text));
    }
}
```

### Index Optimization

```java
public class IndexOptimization {
    
    public static void optimize(EmbeddingStore<?> store) {
        // Rebuild index for better performance
        store.rebuildIndex();
        
        // Set appropriate metadata
        store.setMetadata(Map.of(
            "created_at", Instant.now(),
            "version", "1.0"
        ));
    }
}
```

### Error Handling

```java
@Service
public class ResilientVectorService {
    
    public List<SearchResult> safeSearch(String query) {
        try {
            return store.search(query);
        } catch (Exception e) {
            log.error("Search failed", e);
            return Collections.emptyList();
        }
    }
    
    public void safeAdd(Document doc) {
        try {
            vectorStore.add(doc);
        } catch (RateLimitException e) {
            log.warn("Rate limited, retrying");
            // Implement retry
        }
    }
}
```

---

## Summary

Vector databases are essential for AI applications:

1. **Embed** data into vectors using embedding models
2. **Store** vectors in specialized databases
3. **Search** using similarity metrics (cosine, euclidean)
4. **Integrate** with RAG for LLM context

Popular options: Pinecone, Weaviate, Chroma, Milvus

---

*Continue to QUIZZES.md and EDGE_CASES.md.*
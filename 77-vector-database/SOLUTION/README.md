# Vector Databases - Solution

## Overview

This module provides comprehensive examples for working with vector databases including Pinecone, Milvus, and Chroma. It covers vector storage, similarity search, embeddings, and hybrid search.

## Dependencies

```xml
<dependency>
    <groupId>io.pinecone</groupId>
    <artifactId>pinecone-client</artifactId>
    <version>4.1.0</version>
</dependency>
<dependency>
    <groupId>io.milvus</groupId>
    <artifactId>milvus-sdk-java</artifactId>
    <version>2.3.7</version>
</dependency>
<dependency>
    <groupId>io.trycatcher</groupId>
    <artifactId>chroma-client</artifactId>
    <version>0.4.24</version>
</dependency>
<dependency>
    <groupId>dev.langchain4j</groupId>
    <artifactId>langchain4j-embeddings</artifactId>
    <version>0.35.0</version>
</dependency>
```

## Key Concepts

### 1. Pinecone

The `PineconeExample` class demonstrates:
- Client creation with API key
- Index connection management
- Vector upsertion
- Similarity query with filters
- Vector deletion
- Namespace management

### 2. Milvus

The `MilvusExample` class covers:
- Client creation
- Collection creation
- Vector insertion
- Similarity search
- Collection deletion

### 3. Chroma

The `ChromaExample` class provides:
- Client configuration
- Collection creation
- Document addition
- Query execution
- Collection management

### 4. Embeddings

The `EmbeddingExample` class implements:
- Embedding model creation
- Single text embedding
- Batch embedding generation
- Cosine similarity calculation

### 5. In-Memory Store

The `InMemoryVectorStoreExample` class provides:
- In-memory embedding store
- Embedding addition
- Relevant embedding retrieval

### 6. Hybrid Search

The `HybridSearchExample` class demonstrates:
- Semantic search integration
- Keyword filtering
- Result fusion

## Classes Overview

| Class | Description |
|-------|-------------|
| `PineconeExample` | Pinecone vector database |
| `MilvusExample` | Milvus vector database |
| `ChromaExample` | Chroma vector database |
| `EmbeddingExample` | Embedding generation |
| `InMemoryVectorStoreExample` | In-memory vector storage |
| `HybridSearchExample` | Hybrid search implementation |

## Running Tests

```bash
cd 77-vector-database
mvn test -Dtest=Test
```

## Examples

### Pinecone Upsert

```java
List<String> ids = Arrays.asList("id1", "id2");
List<float[]> vectors = Arrays.asList(vec1, vec2);
connection.upsert(ids, vectors, metadata);
```

### Milvus Search

```java
SearchParam param = SearchParam.newBuilder()
    .withCollectionName("my_collection")
    .withSearchVectors(Collections.singletonList(queryVector))
    .withTopK(10)
    .build();
```

### Chroma Query

```java
List<ChromaResponse> results = client.collection("my_collection")
    .query(5, Collections.singletonList("search text"), null);
```

## Best Practices

1. **Dimension Matching**: Ensure embedding dimensions match index configuration
2. **Index Optimization**: Use appropriate metric type (cosine, euclidean, dot product)
3. **Batch Operations**: Use batch operations for better performance
4. **Filtering**: Apply metadata filters to narrow results
5. **Hybrid Search**: Combine semantic and keyword search for better results

## Further Reading

- [Pinecone Documentation](https://docs.pinecone.io/)
- [Milvus Documentation](https://milvus.io/docs)
- [Chroma Documentation](https://docs.trychroma.com/)
- [LangChain4j Embeddings](https://docs.langchain4j.dev/)
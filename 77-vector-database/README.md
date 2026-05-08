# 77 - Vector Database

A module for understanding and implementing vector databases for AI applications.

## Overview

Vector databases store and search high-dimensional embeddings, enabling semantic search, RAG systems, and similarity-based applications.

## Key Topics

- **Vector Embeddings**: Text-to-vector conversion
- **Similarity Metrics**: Cosine, Euclidean, Dot product
- **Database Systems**: Pinecone, ChromaDB, Weaviate, Qdrant
- **RAG Applications**: Semantic search for LLMs
- **Embedding Models**: Sentence transformers, OpenAI Ada

## Technology Stack

- Pinecone Client
- ChromaDB Client
- Weaviate Client
- Qdrant Client
- Embedding models

## Getting Started

```bash
cd 77-vector-database
mvn compile exec:java
```

## Requirements

- Java 11+
- Maven 3.6+
- Vector DB service (local or cloud)

## References

- Pinecone Docs: https://docs.pinecone.io/
- ChromaDB: https://docs.trychroma.com/
- Weaviate: https://weaviate.io/developers
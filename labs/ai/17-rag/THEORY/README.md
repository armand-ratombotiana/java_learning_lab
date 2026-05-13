# Retrieval Augmented Generation - Theory

## 1. RAG Overview

### Components
1. **Retriever**: Find relevant docs
2. **Generator**: Produce answer with context

### Why RAG?
- Grounded answers
- Up-to-date knowledge
- Reduces hallucinations

## 2. Document Processing

### Chunking
- Fixed size
- Sentence-based
- Semantic (by topic)

### Embedding
- Convert chunks to vectors
- Store in vector DB

## 3. Retrieval

### Search Methods
- Similarity search (cosine, dot product)
- Hybrid search (dense + sparse)
- Reranking

### Evaluation
- Precision@K
- Recall@K
- RAG-specific metrics

## 4. Augmentation Strategies

### Naive
- Top K chunks → prompt

### Advanced
- Summary augmentation
- Multi-step retrieval
- Query expansion

## 5. Vector Databases

### Options
- Pinecone, Weaviate, Chroma
- FAISS, Milvus
- Elasticsearch

### Indexing
- HNSW, IVFFlat
- Compression
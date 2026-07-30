# RAG System Architecture — Deep Dive Guide

## The RAG Pipeline

```
Document → Chunking → Embedding → Index → Retrieve → Augment → Generate
```

1. **Ingestion**: Documents are chunked and embedded
2. **Indexing**: Embeddings stored in vector database
3. **Retrieval**: Given a query, find relevant chunks
4. **Augmentation**: Inject retrieved chunks into prompt context
5. **Generation**: LLM produces answer grounded in retrieved context

## Chunking Strategies

### Fixed-Size Chunking
- Simple word/token count with overlap
- Pros: Predictable size, easy to implement
- Cons: May split sentences/ideas mid-way

### Sentence-Aware Chunking
- Respects sentence boundaries
- Pros: Preserves semantic units
- Cons: Variable chunk lengths

### Semantic Chunking
- Uses embeddings to detect topic boundaries
- Pros: Best semantic coherence
- Cons: Computationally expensive

## Hybrid Search

Pure vector search may miss exact keyword matches. Pure keyword search (BM25) misses semantic similarity. Hybrid combines both.

### BM25 Formula
```
score = IDF * (tf * (k1 + 1)) / (tf + k1 * (1 - b + b * |d| / avgdl))
```
- tf: term frequency in document
- IDF: inverse document frequency
- k1, b: tuning parameters (typically 1.2, 0.75)

### Weighted Combination
```
combined_score = α * vector_score + (1 - α) * keyword_score
```
- α = 0.7 gives more weight to semantic search
- α = 0.3 gives more weight to keyword matching

## Production Considerations
- Use separators between retrieved chunks in prompt
- Implement re-ranking with cross-encoder models
- Cache frequent queries at the retrieval layer
- Monitor retrieval recall with held-out evaluation set

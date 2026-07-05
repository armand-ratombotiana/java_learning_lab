# Debugging: RAG Platform

## Common Issues

### Poor answer quality (irrelevant or wrong)
- Check retrieved chunks: are semantically relevant documents being found? Try searching with query directly
- Debug: log all retrieved chunks and their scores
- Reranker may be misconfigured (wrong model, low threshold)

### Retrieved chunks not relevant
- Embedding model mismatch: query and documents must use same model
- Chunk size too small: chunks may lack sufficient context
- Content not in vector DB: check ingestion pipeline completed

### Empty answers or refusals
- LLM may not have enough context to answer
- System prompt may over-constrain the model
- Check if retrieved context is empty

### Slow query response
- Embedding computation time: use cached query embeddings
- Vector search latency: check HNSW parameters
- LLM generation time: use streaming response

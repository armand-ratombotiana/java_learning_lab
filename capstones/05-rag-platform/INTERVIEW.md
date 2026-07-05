# Interview: RAG Platform

## Common Questions

### Q: Design a RAG system for a customer support knowledge base.
Ingest support articles (PDF/HTML), chunk by sections, embed with Sentence-BERT, store in vector DB. Query: embed question, retrieve top-20, rerank top-5, build prompt with context, generate answer with citations.

### Q: How do you handle documents that are too large for the LLM context window?
Chunk documents, retrieve only relevant chunks, and use a sliding window approach. If chunks exceed context, use summarization or truncation with priority scoring.

### Q: How do you evaluate RAG quality without ground truth data?
Use RAGAS metrics: Faithfulness (LLM as judge), Answer Relevancy, Context Precision. For offline eval, create test set with question + expected answer.

### Q: How would you scale RAG to 1 million documents?
Partition documents by collection/namespace. Use ANN index for retrieval with HNSW. Pre-compute embeddings in batch. Use GPU for embedding inference. Cache frequent queries.

### Q: How do you handle real-time document updates?
Use event-driven architecture: document update triggers re-ingestion, re-chunking, re-embedding, and re-indexing only the changed portion. Keep version metadata to invalidate old cache entries.

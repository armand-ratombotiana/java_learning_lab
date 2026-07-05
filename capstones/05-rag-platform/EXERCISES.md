# Exercises: RAG Platform

## Beginner
1. Implement a simple fixed-size chunker (500 characters with 100 overlap)
2. Add a new document format parser (e.g., DOCX using Apache POI)
3. Implement a response cache for exact-match queries

## Intermediate
4. Add hybrid search: combine BM25 (Lucene) with dense embedding search
5. Implement HyDE (Hypothetical Document Embedding) for query expansion
6. Add streaming response (SSE) for LLM generation
7. Build a RAGAS evaluation pipeline to measure answer quality

## Advanced
8. Implement multi-hop RAG: retrieve -> generate sub-questions -> retrieve more
9. Add agentic RAG: LLM decides when to retrieve and which tools to use
10. Implement GraphRAG: build knowledge graph from documents for structured retrieval
11. Build a real-time document sync watcher that re-indexes on file changes

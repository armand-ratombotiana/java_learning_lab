# How It Works: RAG Platform

1. User uploads a document (PDF, HTML, Markdown, plain text)
2. Document ingestion pipeline: parse -> clean -> chunk
3. Each chunk is embedded using Sentence-BERT (bi-encoder)
4. Embeddings are stored in the Vector Database with metadata (source, page, chunk index)
5. User submits a query (POST /api/v1/query)
6. Query is embedded using the same model
7. Vector DB searches for top-K nearest chunks (K=20)
8. Reranker (cross-encoder) scores query + each chunk pair
9. Top chunks after reranking (top-5) are selected
10. Prompt is constructed: system prompt + retrieved chunks + user query
11. LLM generates answer based on provided context
12. Answer + citations are returned to the user

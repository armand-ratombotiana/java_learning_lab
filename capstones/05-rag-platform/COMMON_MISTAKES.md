# Common Mistakes: RAG Platform

- **Poor chunking strategy**: Chunks too large include irrelevant content, too small lose context. Use recursive splitting with overlap.
- **Not including citations**: Users can't verify LLM claims. Always return source chunk references with answers.
- **Reranking everything**: Reranker is O(K*d) where d is cross-encoder cost. Only rerank top-50, not all documents.
- **Missing query rewriting**: User queries are often ambiguous. Use HyDE (Hypothetical Document Embeddings) or query expansion.
- **No evaluation pipeline**: RAG quality degrades over time. Use RAGAS metrics to monitor faithfulness, relevancy.
- **Context window overflow**: Retrieved chunks + prompt exceed LLM context window. Truncate or summarize contexts.
- **Stale embeddings**: Documents updated but embeddings not recomputed. Implement webhook for re-indexing.

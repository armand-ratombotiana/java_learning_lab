# Edge Cases & Pitfalls: RAG & Vector Databases

When building Retrieval-Augmented Generation systems, several edge cases and architectural pitfalls must be addressed to ensure reliability and accuracy.

## 1. The "Lost in the Middle" Phenomenon
LLMs often struggle to extract information located in the middle of a large context window. If a vector search returns 10 chunks, and the most relevant chunk is placed in the middle of the prompt, the LLM might ignore it.
*   **Mitigation**: Re-ranking algorithms (e.g., Cohere Rerank) can reorder retrieved chunks to place the most relevant ones at the very beginning or end of the context window.

## 2. Poor Chunking Strategy
If documents are chunked arbitrarily (e.g., exactly every 500 characters), a sentence or paragraph might be split in half, destroying the semantic meaning.
*   **Mitigation**: Use semantic splitters or overlap strategies (e.g., chunk size 500, overlap 50) to ensure context is preserved across chunk boundaries.

## 3. Stale Data in Vector Stores
Vector databases do not automatically sync with your primary database. If a document is updated or deleted in the main SQL database, the vector database might still serve the old embedding.
*   **Mitigation**: Implement a robust event-driven architecture (e.g., using Spring Cloud Stream or Debezium) to trigger embedding updates/deletes when source data changes.

## 4. Embedding Model Mismatches
If you ingest data using `text-embedding-3-small` and later query the database using a different model (or an updated version of the same model), the vector math will fail completely.
*   **Mitigation**: Store the embedding model version alongside the vectors. If migrating models, a full re-indexing of all documents is required.

## 5. Over-Retrieval vs. Under-Retrieval (Top-K tuning)
*   **Top-K too low**: The LLM doesn't get enough context to answer the question, leading to hallucinations.
*   **Top-K too high**: The context window is filled with irrelevant noise, increasing API costs, latency, and the risk of confusing the LLM.
*   **Mitigation**: Implement dynamic Top-K based on query complexity or use Metadata Filtering (e.g., filtering by `document_type=policy` before doing the vector search) to narrow the search space.
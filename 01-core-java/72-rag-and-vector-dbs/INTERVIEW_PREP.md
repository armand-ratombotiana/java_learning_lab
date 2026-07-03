# Interview Preparation: RAG & Vector Databases

This document covers advanced questions related to RAG architectures, vector databases, and system design for AI applications.

## Q1: When would you choose RAG over Fine-Tuning an LLM?
**Answer:**
*   **RAG** is preferred when:
    *   The data changes frequently (you can just update the vector DB without retraining the model).
    *   You need strict access control (you can filter vector search results based on user permissions before passing context to the LLM).
    *   You need citations/provenance (you know exactly which chunk of text the LLM used to generate the answer).
    *   Cost and time are constraints (setting up a vector DB is much cheaper and faster than fine-tuning).
*   **Fine-Tuning** is preferred when:
    *   You need the model to learn a new tone, style, or specific output format (e.g., generating code in a proprietary language).
    *   The domain vocabulary is entirely foreign to the base model.
*   **Note**: They are not mutually exclusive. Often, companies fine-tune a model for structure/tone and use RAG for factual grounding.

## Q2: Explain the difference between HNSW and IVF-Flat indexes in a Vector Database.
**Answer:**
*   **IVF-Flat (Inverted File Flat)**: Divides the vector space into clusters (Voronoi cells). During a search, it identifies the nearest cluster centers and only searches within those clusters. It is faster than exact search (KNN) but can miss the true nearest neighbor if it lies just across a cluster boundary.
*   **HNSW (Hierarchical Navigable Small World)**: A graph-based approach. It builds a multi-layered graph where upper layers have fewer, longer connections (fast traversal) and lower layers are dense. It offers extremely fast search times and high recall, making it the default choice for most modern vector databases, though it consumes more memory to store the graph structure.

## Q3: How do you handle documents that exceed the LLM's context window even after vector retrieval?
**Answer:**
If retrieving the top-K chunks exceeds the token limit, several strategies can be employed:
1.  **Map-Reduce**: Send each chunk to the LLM individually to extract an intermediate answer, then send all intermediate answers to the LLM for a final summary.
2.  **Refine**: Send the first chunk to get an answer, then pass that answer + the next chunk to the LLM to "refine" the answer, iterating through all chunks.
3.  **Strict Top-K Reduction**: Simply reduce `K` to fit the window, though this risks losing relevant context.
4.  **Reranking**: Use a reranker model to heavily score and filter the retrieved chunks, ensuring only the absolute most critical text makes it into the prompt.

## Q4: What is "Hybrid Search" in the context of RAG?
**Answer:**
Hybrid search combines traditional keyword-based search (e.g., BM25/TF-IDF) with vector-based semantic search. 
*   **Vector search** is great for conceptual matching (e.g., matching "how to cancel" with "termination policy").
*   **Keyword search** is better for exact matches (e.g., searching for a specific product ID like "TX-99201" or an exact error code).
Hybrid search runs both queries, normalizes the scores, and merges the results (often using Reciprocal Rank Fusion - RRF) to provide the best of both worlds.

## Q5: How do you evaluate the quality of a RAG system?
**Answer:**
Evaluating RAG requires assessing both the *Retrieval* phase and the *Generation* phase. Frameworks like RAGAS use LLMs as judges to evaluate metrics such as:
*   **Context Relevance**: Did the vector DB retrieve information that is actually relevant to the question?
*   **Context Precision**: Were the relevant chunks ranked highly?
*   **Faithfulness**: Is the generated answer derived *only* from the retrieved context, or did the LLM hallucinate?
*   **Answer Relevance**: Does the generated answer actually address the user's original query?
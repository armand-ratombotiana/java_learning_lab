# RAG Internals: Ingestion & Retrieval

## 📥 Phase 1: Data Ingestion (The Pipeline)
Before you can retrieve context, you must prepare your data.

1. **Document Loading**: Extracting raw text from PDFs, Word docs, HTML, etc.
2. **Chunking**: An LLM has a limited context window (e.g., 8k tokens). You cannot feed a 500-page PDF into the prompt. You must split the document into smaller "chunks" (e.g., 500 words each).
   - *Overlap*: Chunks usually have a 10-20% overlap to ensure a sentence isn't cut in half, destroying its meaning.
3. **Embedding**: Pass each text chunk through an Embedding Model (like OpenAI's `text-embedding-3-small`). This converts the text into a dense vector (e.g., 1536 dimensions) that captures its semantic meaning.
4. **Vector Storage**: Store the text chunk and its corresponding vector in a Vector Database (like Pinecone, Milvus, or pgvector).

## 🔍 Phase 2: Semantic Retrieval
When the user asks a question:
1. **Embed the Query**: Pass the user's question through the *exact same* Embedding Model used in Phase 1. This produces a Query Vector.
2. **Similarity Search**: The Vector Database calculates the distance (usually **Cosine Similarity**) between the Query Vector and all the Chunk Vectors in the database.
3. **Top-K**: The database returns the top $K$ (e.g., 5) chunks with the highest similarity scores. These are the chunks semantically closest to the user's question.

## 🛠️ Advanced RAG Techniques
Standard RAG often fails on complex queries. Industry solutions include:
- **Query Rewriting / HyDE**: The user's query might be poorly worded. Have an LLM rewrite the query or generate a hypothetical answer, and then embed *that* to search the database.
- **Parent-Child Retrieval**: Embed small chunks (sentences) for highly accurate retrieval, but return the larger parent chunk (the paragraph) to the LLM so it has full context.
- **Re-ranking**: Use a fast, cheap vector search to get the top 50 results, then use a slower, highly accurate Cross-Encoder model (like Cohere Rerank) to resort them and pick the true top 5.
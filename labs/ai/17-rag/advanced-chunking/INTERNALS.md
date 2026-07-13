# Advanced RAG Internals

## 🏗️ Parent-Child Retrieval Architecture
Even with perfect semantic chunking, RAG systems face a fundamental tension:
1. **For Retrieval**: Smaller chunks (e.g., single sentences) result in much more accurate vector searches. A sentence has a highly concentrated semantic meaning. A large paragraph dilutes the meaning (the "Lost in the Middle" problem).
2. **For Generation**: The LLM needs large chunks (e.g., full paragraphs or pages) to synthesize a comprehensive, nuanced answer.

**Parent-Child Retrieval (Auto-Merging Retriever)** solves this tension by separating the chunk used for searching from the chunk given to the LLM.

### The Ingestion Workflow
1. Split the document into large **Parent Chunks** (e.g., 1000 tokens).
2. Assign a unique UUID to each Parent Chunk and store it in a standard Key-Value database (e.g., Redis or DynamoDB). *Do not embed it.*
3. Split each Parent Chunk into multiple small **Child Chunks** (e.g., 100 tokens).
4. Embed the Child Chunks.
5. Store the Child Chunks in the Vector Database, but add a metadata field to each one: `parent_id = <UUID>`.

### The Retrieval Workflow
1. Embed the user's query.
2. Perform a vector search to find the Top 5 most similar **Child Chunks**. (This search is highly accurate because the chunks are small and focused).
3. Look at the `parent_id` metadata of the top Child Chunks.
4. Go to the Key-Value database and fetch the full **Parent Chunks** associated with those IDs.
5. Pass the massive Parent Chunks to the LLM as context.

This architecture gives you the best of both worlds: laser-accurate retrieval and massive context for generation.
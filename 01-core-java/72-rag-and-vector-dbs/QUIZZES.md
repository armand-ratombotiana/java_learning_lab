# Quizzes: RAG & Vector Databases

Test your knowledge on Retrieval-Augmented Generation, embeddings, and vector stores.

## Quiz 1: Core Concepts

**Q1: What is the primary purpose of Retrieval-Augmented Generation (RAG)?**
- A) To train an LLM on new data from scratch.
- B) To reduce the size of the LLM model.
- C) To provide the LLM with relevant, up-to-date external context before generating an answer.
- D) To speed up the token generation process.
*Answer: C*

**Q2: Which of the following is NOT a common metric used for similarity search in a vector database?**
- A) Cosine Similarity
- B) Euclidean Distance (L2)
- C) Dot Product
- D) Levenshtein Distance
*Answer: D (Levenshtein is for string distance, not high-dimensional vectors)*

## Quiz 2: Chunking and Embeddings

**Q1: Why is "overlap" important when chunking documents for RAG?**
- A) It prevents the vector database from running out of memory.
- B) It ensures that semantic context isn't lost if a sentence or paragraph is split across two chunks.
- C) It makes the embedding generation process faster.
- D) It reduces the API cost of the LLM.
*Answer: B*

**Q2: If you change your embedding model from `text-embedding-ada-002` to `text-embedding-3-small`, what must you do to your existing vector database?**
- A) Nothing, the vector database automatically translates the embeddings.
- B) Update the metadata of the existing vectors.
- C) Re-chunk and re-embed all documents using the new model, as the vector spaces are incompatible.
- D) Just restart the Spring Boot application.
*Answer: C*

## Quiz 3: Architecture

**Q1: In a Spring AI RAG application, which component is responsible for finding the most relevant document chunks based on a user's query?**
- A) `ChatClient`
- B) `VectorStore`
- C) `DocumentReader`
- D) `TextSplitter`
*Answer: B*
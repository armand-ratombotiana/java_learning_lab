# Lab 03: RAG System Architecture

## Learning Objectives
- Build a complete retrieval-augmented generation pipeline
- Implement multiple chunking strategies (fixed-size, sentence-aware)
- Combine vector similarity with keyword search (hybrid search)
- Understand the tradeoffs in retrieval quality vs. latency

## Concepts Covered
- **Chunking**: Splitting documents into retrievable units
- **Embedding-Based Retrieval**: Semantic search via vector similarity
- **BM25/Keyword Search**: Lexical matching for precision
- **Hybrid Search**: Weighted combination of vector + keyword scores
- **Re-ranking**: Improving retrieval quality with a second pass

## Setup
```bash
cd lab03
javac src/com/aiengineering/lab03/RagSystemArchitectureDemo.java
java com.aiengineering.lab03.RagSystemArchitectureDemo
```

## Key Takeaways
- Chunk size and overlap significantly impact retrieval quality
- Hybrid search outperforms pure vector or pure keyword search
- Sentence-aware chunking preserves semantic boundaries

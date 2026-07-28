# Lab 04: Interview Questions

## Q1: What are the main components of a RAG system?
**A:** Ingestion pipeline (chunk → embed → index), retriever (ANN search), augmenter (prompt construction), and generator (LLM).

## Q2: How do you choose chunk size and overlap?
**A:** Chunk size depends on document type (256-1024 tokens typical). Overlap (10-20%) preserves boundary context. Evaluate on retrieval recall.

## Q3: What is the difference between sparse (BM25) and dense retrieval?
**A:** Sparse (BM25) uses keyword matching; dense uses embedding similarity. Dense captures semantic meaning; sparse is better for exact term matching. Hybrid approaches combine both.

## Q4: How do you evaluate RAG quality?
**A:** Retrieval recall (hit rate), answer faithfulness (claim overlap with retrieved docs), answer relevance (usefulness to the query).

## Q5: What is the "lost in the middle" problem and how do you mitigate it?
**A:** LLMs tend to ignore middle-placed context. Mitigations: re-rank retrieved docs, place most relevant at start/end, use structured formats.

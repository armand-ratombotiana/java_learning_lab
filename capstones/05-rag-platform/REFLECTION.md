# Reflection: RAG Platform

## What I Learned
- The full RAG pipeline: ingestion -> embedding -> retrieval -> reranking -> generation
- Chunking strategy dramatically impacts retrieval quality
- Bi-encoder vs cross-encoder trade-offs for speed vs accuracy
- Prompt engineering for grounding LLM responses in context

## Challenges
- Tuning chunk size and overlap for different document types
- Debugging cases where retrieval fails to find relevant context
- Managing LLM API costs while maintaining throughput
- Building an evaluation pipeline that catches quality regressions

## What I'd Do Differently
- Build the evaluation pipeline first to establish baseline metrics
- Implement hybrid search (BM25 + embeddings) from the start
- Use streaming responses for better user experience
- Add query rewriting (HyDE) for ambiguous questions

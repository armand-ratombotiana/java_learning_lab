# Theory: RAG Platform

## Retrieval-Augmented Generation
RAG combines a retrieval system (search relevant documents) with a generative model (LLM) to produce answers grounded in retrieved context. This addresses LLM limitations: hallucination, stale knowledge, and lack of domain expertise.

## Architecture Patterns
- **Naive RAG**: Retrieve -> Augment -> Generate (single pass)
- **Advanced RAG**: Pre-retrieval optimization (query rewriting, HyDE), post-retrieval (reranking, filtering)
- **Modular RAG**: Pluggable modules — query transformation, routing, fusion, memory

## Chunking Strategies
- Fixed-size chunking: Simple, predictable, may split semantic units
- Semantic chunking: Split at sentence/paragraph boundaries using embeddings
- Recursive chunking: Try different separators (\n\n, \n, . ) and sizes

## Embedding Models
- Sentence-BERT (all-MiniLM-L6-v2): Good balance of quality and speed
- Instructor-XL: Task-aware embeddings (prefix instructions)
- OpenAI Ada-002: High quality, paid API
- BGE (BAAI): Top-performing open-source embeddings

## Reranking
A cross-encoder model that jointly encodes query + document pair to produce relevance score. Higher quality but slower than bi-encoder embeddings. Applied to top-K retrieved results (K=50-200).

# Mental Models: RAG Platform

- **RAG = Open-Book Exam**: LLM is the student; retrieved documents are the reference book.
- **Chunking = Slicing Knowledge Base**: Like cutting a book into digestible passages.
- **Embedding = Semantic Coordinate**: Each chunk gets coordinates in semantic space.
- **Retrieval = Finding Relevant Pages**: Vector search finds passages near the query in semantic space.
- **Reranking = Quality Check**: Cross-encoder re-reads query + each passage to verify relevance.
- **Augmentation = Highlighting Exam Text**: Add retrieved passages to the prompt as context.
- **Generation = Writing the Answer**: LLM produces final answer based on highlighted context.

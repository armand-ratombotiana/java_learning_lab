# Flashcards: RAG Platform

Front: What is RAG? | Back: Retrieval-Augmented Generation — retrieves relevant documents from a knowledge base and uses them as context for LLM generation, reducing hallucination.

Front: What is the difference between bi-encoder and cross-encoder? | Back: Bi-encoder produces a single vector for query/document (fast, used for retrieval). Cross-encoder jointly encodes query+document (slow, accurate, used for reranking).

Front: What is HyDE? | Back: Hypothetical Document Embedding — generates a hypothetical ideal document from the query and uses its embedding for search, improving retrieval.

Front: What is chunking? | Back: Splitting documents into smaller passages for embedding and retrieval. Strategies: fixed-size, recursive, semantic.

Front: What is RAGAS? | Back: RAG Assessment — a framework for evaluating RAG systems on metrics like faithfulness, answer relevancy, context precision, and context recall.

Front: What is hybrid search? | Back: Combining sparse (BM25, keyword) and dense (embedding, semantic) retrieval with score fusion for better recall.

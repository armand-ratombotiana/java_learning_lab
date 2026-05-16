# RAG (Retrieval Augmented Generation) - FLASHCARDS

### Card 1
**Q:** What is RAG?
**A:** Retrieval Augmented Generation - combines retrieval of relevant documents with LLM generation

### Card 2
**Q:** Why use document chunking?
**A:** Breaks large documents into manageable pieces; LLM has limited context window

### Card 3
**Q:** What is optimal chunk size?
**A:** Depends on use case; 256-512 tokens common; balances context vs precision

### Card 4
**Q:** What is vector store?
**A:** Database storing embeddings for fast similarity search

### Card 5
**Q:** Why use overlap in chunking?
**A:** Preserves context at chunk boundaries; prevents information loss

### Card 6
**Q:** What is hybrid search?
**A:** Combines semantic (embedding) and keyword (BM25) search for better results

### Card 7
**Q:** What does re-ranker do?
**A:** Re-scores retrieved documents using more sophisticated relevance model

### Card 8
**Q:** What is BM25?
**A:** Classical retrieval algorithm using term frequency and document length normalization

### Card 9
**Q:** Why RAG over fine-tuning?
**A:** Can update knowledge without retraining; more flexible; better for current info

### Card 10
**Q:** What is cross-encoder re-ranking?
**A:** Scores query-document pairs together rather than independently; more accurate

### Card 11
**Q:** What is semantic search vs keyword search?
**A:** Semantic: meaning-based using embeddings. Keyword: exact term matching

### Card 12
**Q:** How to handle OOV chunks in retrieval?
**A:** Subword tokenization, higher top-k retrieval, or hybrid approach

**Total: 12 flashcards**
# Common Mistakes: Search Engine

- **No stemming**: User searches "running", index has "run" — no results. Apply Porter stemmer on index and query.
- **Stop words not filtered**: "the", "a", "is" create huge postings lists with little value. Filter them.
- **Case sensitivity**: "Java" vs "java" mismatch. Always lowercase both index and query.
- **Wrong BM25 parameters**: k1=1.2, b=0.75 are defaults for web search. Tune for your corpus.
- **Not normalizing field length**: Long documents with same TF score lower than short documents. Use BM25 field norms.
- **Single segment for large index**: Many small segments hurt query performance. Merge periodically.
- **No query caching**: Frequent identical queries re-execute. Cache results with TTL.

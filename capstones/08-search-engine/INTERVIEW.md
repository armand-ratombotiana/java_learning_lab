# Interview: Search Engine

## Common Questions

### Q: Design a full-text search engine like Lucene.
Inverted index with segments. Analyzer pipeline for tokenization. BM25 ranking. Query parser for Boolean/phrase/fuzzy queries. Background segment merging. Near-real-time indexing with refresh intervals.

### Q: How does BM25 handle long documents better than TF-IDF?
TF-IDF over-rewards high term frequency in long documents. BM25 saturates TF contribution (k1 parameter) and normalizes by document length (b parameter), preventing long documents from dominating.

### Q: How do you handle real-time indexing in a search engine?
Use in-memory buffer for recent documents. Periodically flush to a new segment. Near-real-time: reopen IndexReader to see new segments without full restart. Background merge for optimization.

### Q: How would you scale search to billions of documents?
Shard index across multiple nodes (consistent hashing on doc ID). Replicate shards for read throughput. Use distributed search coordinator (like Elasticsearch). Pre-compute per-shard IDF approximations.

### Q: How do you support faceted search (aggregations)?
Store field values in column-oriented format (doc values). Use bitsets for filter intersections. Pre-compute term/dimension counts during index time with ordinal maps.

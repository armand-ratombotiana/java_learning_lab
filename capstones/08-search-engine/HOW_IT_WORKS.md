# How It Works: Search Engine

1. Document is indexed: tokenized -> analyzed -> added to inverted index
2. Inverted index maps each term -> list of (docId, frequency, positions)
3. Index is split into segments (write-once, immutable)
4. Query arrives: parsed into a query tree (TermQuery, BooleanQuery, PhraseQuery)
5. Query tree is evaluated against the inverted index
6. Matching documents are collected with term frequencies
7. BM25 score is computed for each match
8. Top-N results sorted by score are returned
9. Segments are merged in background to keep index compact
10. Deleted documents are removed during merge

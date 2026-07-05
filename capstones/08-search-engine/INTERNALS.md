# Internals: Search Engine

## Core Components
- **Analyzer**: Tokenizer + filters (lowercase, stop-word removal, stemming)
- **IndexWriter**: Creates/updates/deletes documents; manages segments
- **IndexReader**: Reads segments for query execution
- **IndexSearcher**: Evaluates query trees against index, collects hits
- **QueryParser**: Parses query string into query tree
- **SegmentMerge**: Background merger for optimizing segment count

## Inverted Index Structure
- Terms dictionary: sorted list of all terms (prefix-coded, via FST)
- Postings list: for each term, list of (docId, termFrequency, positions[])
- Term vectors: for each document, list of terms with frequencies (for highlighting)
- Field norms: length normalization factor per field per document (for BM25)

## Index Segment (.idx)
- Segment info: name, doc count, maxDoc
- Terms dictionary (.tim): FST (Finite State Transducer) for term lookup
- Postings (.pos): doc IDs + frequencies, optionally positions
- Norms (.nvm): field normalization factors
- Stored fields (.fld): original document fields (returned in results)
